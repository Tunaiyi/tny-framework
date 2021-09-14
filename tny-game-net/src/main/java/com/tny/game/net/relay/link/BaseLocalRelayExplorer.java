package com.tny.game.net.relay.link;

import com.google.common.collect.*;
import com.tny.game.common.result.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.base.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.exception.*;
import com.tny.game.net.relay.link.route.*;
import com.tny.game.net.transport.*;
import org.slf4j.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.relay.cluster.ServeClusterFilterStatus.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 9:00 下午
 */
public abstract class BaseLocalRelayExplorer<T extends NetLocalServeCluster> extends BaseRelayExplorer<LocalRelayTunnel<?>> implements NetLocalRelayExplorer {

	public static final Logger LOGGER = LoggerFactory.getLogger(BaseLocalRelayExplorer.class);

	private volatile List<T> clusters = ImmutableList.of();

	private volatile Map<String, T> clusterMap = ImmutableMap.of();

	private final LocalRelayContext context;

	public BaseLocalRelayExplorer(LocalRelayContext localRelayContext) {
		this.context = localRelayContext;
	}

	protected BaseLocalRelayExplorer<T> initClusters(Collection<T> clusters) {
		this.clusters = ImmutableList.copyOf(clusters);
		this.clusterMap = ImmutableMap.copyOf(clusters.stream().collect(Collectors.toMap(NetLocalServeCluster::getServeName, ObjectAide::self)));
		return this;
	}

	@Override
	public LocalServeCluster getCluster(String id) {
		return clusterMap.get(id);
	}

	@Override
	public List<LocalServeCluster> getClusters() {
		return as(clusters);
	}

	@Override
	public <D> DoneResult<LocalRelayTunnel<D>> createTunnel(long id, MessageTransporter<D> transport, NetworkContext context) {
		RelayMessageRouter relayMessageRouter = this.context.getRelayMessageRouter();
		GeneralLocalRelayTunnel<D> tunnel = new GeneralLocalRelayTunnel<>(this.context.getCurrentInstanceId(), id, transport, context,
				relayMessageRouter);
		Map<String, LocalTunnelRelayer> relayer = preassignRelayer(tunnel);
		tunnel.initRelayers(relayer);
		return DoneResults.success(putTunnel(tunnel));
	}

	@Override
	public <D> LocalRelayLink allotLink(LocalRelayTunnel<D> tunnel, String serveName) {
		LocalServeCluster cluster = this.getCluster(serveName);
		if (cluster == null) {
			return null;
		}
		return cluster.allotLink(tunnel);
	}

	// 预分配 link
	private Map<String, LocalTunnelRelayer> preassignRelayer(LocalRelayTunnel<?> tunnel) {
		if (this.clusters.size() == 1) {
			return preassignForSingleCluster(tunnel);
		} else {
			return preassignForMultipleCluster(tunnel);
		}
	}

	// 预分配 link, 但集群
	private Map<String, LocalTunnelRelayer> preassignForSingleCluster(LocalRelayTunnel<?> tunnel) {
		for (NetLocalServeCluster cluster : clusters) {
			LocalTunnelRelayer relayer = assignRelayer(tunnel, cluster);
			if (relayer != null) {
				return ImmutableMap.of(relayer.getServeName(), relayer);
			}
		}
		return ImmutableMap.of();
	}

	// 预分配 link, 但多集群
	private Map<String, LocalTunnelRelayer> preassignForMultipleCluster(LocalRelayTunnel<?> tunnel) {
		Map<String, LocalTunnelRelayer> relayerMap = ImmutableMap.of();
		for (NetLocalServeCluster cluster : clusters) {
			LocalTunnelRelayer relayer = assignRelayer(tunnel, cluster);
			if (relayer != null) {
				relayerMap.put(relayer.getServeName(), relayer);
			}
		}
		return relayerMap;
	}

	private LocalTunnelRelayer assignRelayer(LocalRelayTunnel<?> tunnel, NetLocalServeCluster cluster) {
		ServeClusterFilter serveClusterFilter = context.getServeClusterFilter();
		ServeClusterFilterStatus filterStatus = serveClusterFilter.filter(tunnel, cluster); // 过滤器
		if (filterStatus == UNNECESSARY) {
			return null;
		}
		LocalTunnelRelayer relayer = new LocalTunnelRelayer(cluster.getServeName(), filterStatus, this);
		LocalRelayLink link = relayer.allot(tunnel); // 分配
		if ((link == null || !link.isActive())) {
			if (filterStatus == ServeClusterFilterStatus.REQUIRED) {
				throw new RelayLinkNoExistException("Tunnel[{}] 申请分配 {} cluster 集群无可用连接", tunnel.getRemoteAddress(), cluster.getServeName());
			} else {
				LOGGER.warn("Tunnel[{}] 申请分配 {} cluster 集群无可用连接", tunnel.getRemoteAddress(), cluster.getServeName());
			}
		}
		return relayer;
	}

	protected T clusterOf(String id) {
		return clusterMap.get(id);
	}

	protected List<T> clusters() {
		return clusters;
	}

}
