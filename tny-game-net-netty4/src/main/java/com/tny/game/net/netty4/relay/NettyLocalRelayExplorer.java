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

	private final List<NettyLocalServeClusterContext> clusterContexts;

	private final LocalRelayContext localRelayContext;

	public NettyLocalRelayExplorer(LocalRelayContext localRelayContext, List<NettyLocalServeClusterContext> clusterContexts) {
		super(localRelayContext);
		this.localRelayContext = localRelayContext;
		this.clusterContexts = clusterContexts;
	}

	@Override
	public PrepareStarter getPrepareStarter() {
		return PrepareStarter.value(NettyLocalRelayExplorer.class, LifecycleLevel.SYSTEM_LEVEL_9);
	}

	@Override
	public void prepareStart() throws Exception {
		Set<NettyLocalServeCluster> clusters = clusterContexts.stream()
				.map(ctx -> new NettyLocalServeCluster(ctx, localRelayContext))
				.collect(Collectors.toSet());
		this.initClusters(clusters);
		for (NettyLocalServeClusterContext clusterContext : clusterContexts) {
			NettyLocalServeCluster cluster = this.clusterOf(clusterContext.getId());
			for (ServeNode node : clusterContext.getNodes()) {
				NettyServeInstanceConnector connector = new NettyServeInstanceConnector(localRelayContext, clusterContext, executorService);
				LocalServeInstance instance = new NettyLocalServeInstance(cluster, node, connector);
				connector.start(instance, clusterContext.getLinkConnectionSize());
				cluster.registerInstance(instance);
			}
			long heartbeatInterval = clusterContext.getLinkHeartbeatInterval();
			if (heartbeatInterval > 0) {
				executorService.scheduleWithFixedDelay(cluster::heartbeat, heartbeatInterval, heartbeatInterval, TimeUnit.MILLISECONDS);
			}
		}
	}

	@Override
	public void onClosed() {
		for (LocalServeCluster cluster : getClusters()) {
			cluster.close();
		}
	}

}
