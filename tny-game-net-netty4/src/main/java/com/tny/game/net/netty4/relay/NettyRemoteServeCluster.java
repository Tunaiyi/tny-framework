package com.tny.game.net.netty4.relay;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.net.relay.link.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 8:49 下午
 */
public class NettyRemoteServeCluster extends BaseRemoteServeCluster {

	//	private final LocalRelayContext relayContext;

	private final RemoteServeClusterContext clusterContext;

	public NettyRemoteServeCluster(RemoteServeClusterContext clusterContext) {
		super(clusterContext.getServeName(),
				clusterContext.getUsername(),
				clusterContext.getServeInstanceAllotStrategy(),
				clusterContext.getRelayLinkAllotStrategy());
		this.clusterContext = clusterContext;
	}

	//	public String getCurrentServeName() {
	//		return relayContext.getCurrentServeName();
	//	}
	//
	//	public long getCurrentInstanceId() {
	//		return relayContext.getCurrentInstanceId();
	//	}

	@Override
	public RemoteServeClusterContext getContext() {
		return this.clusterContext;
	}

	public void heartbeat() {
		for (NetRemoteServeInstance instance : this.instances()) {
			ExeAide.runQuietly(instance::heartbeat, LOGGER);
		}
	}

	//	/**
	//	 * @param url url
	//	 */
	//	public void connect(URL url, RelayConnectCallback callback) {
	//		guide.connect(url, callback);
	//	}
	//
	//	/**
	//	 * @param url url
	//	 */
	//	public void connect(URL url, long delayTime, RelayConnectCallback callback) {
	//		executorService.schedule(() -> guide.connect(url, callback), delayTime, TimeUnit.MILLISECONDS);
	//	}

}
