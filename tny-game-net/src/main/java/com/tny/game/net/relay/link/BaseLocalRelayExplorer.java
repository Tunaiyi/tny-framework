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
import static com.tny.game.net.relay.cluster.ServeClusterImportance.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 9:00 下午
 */
public abstract class BaseLocalRelayExplorer<T extends LocalServeCluster> extends BaseRelayExplorer<LocalRelayTunnel<?>> implements LocalRelayExplorer {

	public static final Logger LOGGER = LoggerFactory.getLogger(BaseLocalRelayExplorer.class);

	private volatile List<T> clusters = ImmutableList.of();

	private volatile Map<String, T> clusterMap = ImmutableMap.of();

	private final LocalRelayContext context;

	public BaseLocalRelayExplorer(LocalRelayContext localRelayContext) {
		this.context = localRelayContext;
	}

	protected BaseLocalRelayExplorer<T> initClusters(Collection<T> clusters) {
		this.clusters = ImmutableList.copyOf(clusters);
		this.clusterMap = ImmutableMap.copyOf(clusters.stream().collect(Collectors.toMap(LocalServeCluster::getId, ObjectAide::self)));
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
	public Map<String, LocalServeCluster> getClusterMap() {
		return as(clusterMap);
	}

	@Override
	public <D> DoneResult<LocalRelayTunnel<D>> createTunnel(long id, MessageTransporter<D> transport, NetworkContext context) {
		RelayMessageRouter relayMessageRouter = this.context.getRelayMessageRouter();
		GeneralLocalRelayTunnel<D> tunnel = new GeneralLocalRelayTunnel<>(
				this.context.getInstanceId(), id, transport, context, this, relayMessageRouter);
		List<LocalRelayLink> links = preassignLinks(tunnel);
		for (LocalRelayLink link : links) {
			tunnel.bindLink(link);
		}
		return DoneResults.success(putTunnel(tunnel));
	}

	@Override
	public <D> LocalRelayLink allotLink(LocalRelayTunnel<D> tunnel, String clusterId) {
		LocalServeCluster cluster = this.getCluster(clusterId);
		if (cluster == null) {
			return null;
		}
		LocalRelayLink link = cluster.allotLink(tunnel);
		tunnel.bindLink(link);
		return link;
	}

	private List<LocalRelayLink> preassignLinks(NetTunnel<?> tunnel) {
		if (this.clusters.size() == 1) {
			return preassignLinksOnSign(tunnel);
		} else {
			return preassignLinksOnMultiple(tunnel);
		}
	}

	private List<LocalRelayLink> preassignLinksOnSign(NetTunnel<?> tunnel) {
		for (LocalServeCluster cluster : clusters) {
			LocalRelayLink link = assignLinks(tunnel, cluster);
			if (link != null) {
				return Collections.singletonList(link);
			}
		}
		return Collections.emptyList();
	}

	private List<LocalRelayLink> preassignLinksOnMultiple(NetTunnel<?> tunnel) {
		List<LocalRelayLink> links = ImmutableList.of();
		for (LocalServeCluster cluster : clusters) {
			LocalRelayLink link = assignLinks(tunnel, cluster);
			if (link != null) {
				links.add(link);
			}
		}
		return links;
	}

	private LocalRelayLink assignLinks(NetTunnel<?> tunnel, LocalServeCluster cluster) {
		ServeClusterFilter serveClusterSelector = context.getServeClusterFilter();
		ServeClusterImportance importance = serveClusterSelector.select(tunnel, cluster);
		if (importance == UNNECESSARY) {
			return null;
		}
		LocalRelayLink link = cluster.allotLink(tunnel);
		if ((link == null || !link.isActive())) {
			if (importance == ServeClusterImportance.REQUIRED) {
				throw new RelayLinkNoExistException("Tunnel[{}] 申请分配 {} cluster 集群无可用连接", tunnel.getRemoteAddress(), cluster.getId());
			} else {
				LOGGER.warn("Tunnel[{}] 申请分配 {} cluster 集群无可用连接", tunnel.getRemoteAddress(), cluster.getId());
			}
		}
		return link;
	}

	protected T clusterOf(String id) {
		return clusterMap.get(id);
	}

}
