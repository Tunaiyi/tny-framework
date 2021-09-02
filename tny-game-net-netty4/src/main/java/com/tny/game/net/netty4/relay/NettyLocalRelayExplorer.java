package com.tny.game.net.netty4.relay;

import com.tny.game.common.lifecycle.*;
import com.tny.game.net.base.*;
import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;
import org.slf4j.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 2:29 下午
 */
public class NettyLocalRelayExplorer extends BaseLocalRelayExplorer<NettyLocalServeCluster> implements AppPrepareStart, AppClosed {

	public static final Logger LOGGER = LoggerFactory.getLogger(NettyLocalRelayExplorer.class);

	private final List<RelayServeClusterContext> clusterContexts;

	private final NetAppContext appContext;

	public NettyLocalRelayExplorer(NetAppContext appContext, List<RelayServeClusterContext> clusterContexts) {
		this.appContext = appContext;
		this.clusterContexts = clusterContexts;
	}

	@Override
	public PrepareStarter getPrepareStarter() {
		return PrepareStarter.value(NettyLocalRelayExplorer.class, LifecycleLevel.SYSTEM_LEVEL_9);
	}

	@Override
	public void prepareStart() throws Exception {
		Set<NettyLocalServeCluster> clusters = clusterContexts.stream()
				.map(ctx -> new NettyLocalServeCluster(ctx, appContext))
				.collect(Collectors.toSet());
		this.initClusters(clusters);
		for (RelayServeClusterContext clusterContext : clusterContexts) {
			NettyLocalServeCluster cluster = getCluster(clusterContext.getId());
			for (ServeNode node : clusterContext.getNodes()) {
				NettyServeInstance instance = cluster.loadInstance(node);
				instance.connect();
			}
		}
	}

	@Override
	public void onClosed() {
		for (NettyLocalServeCluster cluster : getClusters()) {
			cluster.close();
		}
	}

}
