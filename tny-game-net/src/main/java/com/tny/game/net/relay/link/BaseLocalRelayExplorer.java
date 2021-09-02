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

import static com.tny.game.net.relay.cluster.ServeClusterImportance.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 9:00 下午
 */
public abstract class BaseLocalRelayExplorer<T extends LocalServeCluster> implements LocalRelayExplorer {

	public static final Logger LOGGER = LoggerFactory.getLogger(BaseLocalRelayExplorer.class);

	private volatile List<T> clusters = ImmutableList.of();

	private volatile Map<String, T> clusterMap = ImmutableMap.of();

	private RelayMessageRouter relayMessageRouter = new FirstRelayMessageRouter();

	private LocalServeClusterSelector localServeClusterSelector = new AllRequiredLocalServeClusterSelector();

	public BaseLocalRelayExplorer() {
	}

	protected BaseLocalRelayExplorer<T> initClusters(Collection<T> clusters) {
		this.clusters = ImmutableList.copyOf(clusters);
		this.clusterMap = ImmutableMap.copyOf(clusters.stream().collect(Collectors.toMap(LocalServeCluster::getId, ObjectAide::self)));
		return this;
	}

	protected T getCluster(String id) {
		return clusterMap.get(id);
	}

	protected List<T> getClusters() {
		return clusters;
	}

	@Override
	public <I> DoneResult<LocalRelayTunnel<I>> bindTunnel(NetTunnel<I> tunnel) {
		Map<String, TunnelRelayLinker> relayLinks = allot(tunnel);
		if (relayLinks.isEmpty()) {
			return DoneResults.failure(NetResultCode.CLUSTER_NETWORK_UNCONNECTED_ERROR);
		}
		return DoneResults.success(new GeneralLocalRelayTunnel<>(tunnel, relayLinks, relayMessageRouter));
	}

	private Map<String, TunnelRelayLinker> allot(NetTunnel<?> tunnel) {
		ImmutableMap.Builder<String, TunnelRelayLinker> builder = ImmutableMap.builder();
		for (LocalServeCluster cluster : clusters) {
			ServeClusterImportance importance = localServeClusterSelector.route(tunnel, cluster);
			if (importance == UNNECESSARY) {
				continue;
			}
			LocalRelayLink link = cluster.allotLink(tunnel);
			if ((link == null || !link.isActive())) {
				if (importance == ServeClusterImportance.REQUIRED) {
					throw new RelayLinkNoExistException("Tunnel[{}] 申请分配 {} cluster 集群无可用连接", tunnel.getRemoteAddress(), cluster.getId());
				} else {
					LOGGER.warn("Tunnel[{}] 申请分配 {} cluster 集群无可用连接", tunnel.getRemoteAddress(), cluster.getId());
				}
			}
			if (link != null) {
				builder.put(link.getId(), new TunnelRelayLinker(link, importance));
			}
		}
		return builder.build();
	}

	public BaseLocalRelayExplorer<T> setRelayMessageRouter(RelayMessageRouter relayMessageRouter) {
		this.relayMessageRouter = relayMessageRouter;
		return this;
	}

	protected BaseLocalRelayExplorer<T> setLocalServeClusterSelector(LocalServeClusterSelector localServeClusterSelector) {
		this.localServeClusterSelector = localServeClusterSelector;
		return this;
	}

}
