package com.tny.game.net.netty4.relay;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.lifecycle.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 2:29 下午
 */
@Unit
public class NettyLocalRelayExplorer extends BaseLocalRelayExplorer<NettyLocalServeCluster> implements AppPrepareStart, AppClosed {

	public static final Logger LOGGER = LoggerFactory.getLogger(NettyLocalRelayExplorer.class);

	private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1,
			new CoreThreadFactory("RelayReconnectScheduled"));

	private final LocalRelayContext localRelayContext;

	public NettyLocalRelayExplorer(LocalRelayContext localRelayContext, List<NettyLocalServeClusterContext> clusterContexts) {
		super(localRelayContext);
		this.localRelayContext = localRelayContext;
		Set<NettyLocalServeCluster> clusters = clusterContexts.stream()
				.map(NettyLocalServeCluster::new)
				.collect(Collectors.toSet());
		this.initClusters(clusters);
	}

	@Override
	public PrepareStarter getPrepareStarter() {
		return PrepareStarter.value(NettyLocalRelayExplorer.class, LifecycleLevel.SYSTEM_LEVEL_9);
	}

	@Override
	public void putInstance(ServeNode node) {
		NettyLocalServeCluster cluster = this.clusterOf(node.getServeName());
		if (cluster != null) {
			addInstance(node, cluster);
		}
	}

	@Override
	public void removeInstance(ServeNode node) {
		NettyLocalServeCluster cluster = this.clusterOf(node.getServeName());
		if (cluster != null) {
			cluster.unregisterInstance(node.getId());
		}
	}

	/**
	 * 变健康
	 */
	@Override
	public void updateInstance(ServeNode node, List<ServeNodeChangeStatus> statuses) {
		NettyLocalServeCluster cluster = this.clusterOf(node.getServeName());
		if (cluster != null) {
			if (statuses.contains(ServeNodeChangeStatus.URL_CHANGE)) {
				cluster.unregisterInstance(node.getId());
				this.putInstance(node);
				return;
			}
			if (statuses.contains(ServeNodeChangeStatus.METADATA_CHANGE)) {
				cluster.updateInstance(node);
			}
		}
	}

	private void addInstance(ServeNode node, NettyLocalServeCluster cluster) {
		LocalServeClusterContext context = cluster.getContext();
		NettyServeInstanceConnectMonitor connectMonitor = new NettyServeInstanceConnectMonitor(localRelayContext, context, executorService);
		NetLocalServeInstance instance = new NettyLocalServeInstance(cluster, node, connectMonitor);
		connectMonitor.start(instance, context.getLinkConnectionSize());
		cluster.registerInstance(instance);
	}

	@Override
	public void prepareStart() throws Exception {
		for (NettyLocalServeCluster cluster : this.clusters()) {
			LocalServeClusterContext clusterContext = cluster.getContext();
			long heartbeatInterval = clusterContext.getLinkHeartbeatInterval();
			if (heartbeatInterval > 0) {
				executorService.scheduleWithFixedDelay(cluster::heartbeat, heartbeatInterval, heartbeatInterval, TimeUnit.MILLISECONDS);
			}
		}
	}

	@Override
	public void onClosed() {
		for (NetLocalServeCluster cluster : clusters()) {
			cluster.close();
		}
	}

}
