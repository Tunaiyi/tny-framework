package com.tny.game.net.netty4.relay;

import com.tny.game.common.concurrent.utils.*;
import com.tny.game.net.relay.link.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 8:49 下午
 */
public class NettyLocalServeCluster extends BaseLocalServeCluster {

	private final LocalRelayContext relayContext;

	private final LocalServeClusterContext clusterContext;

	public NettyLocalServeCluster(LocalServeClusterContext clusterContext, LocalRelayContext relayContext) {
		super(clusterContext.getId(), clusterContext.getServeInstanceAllotStrategy(), clusterContext.getRelayLinkAllotStrategy());
		this.clusterContext = clusterContext;
		this.relayContext = relayContext;

	}

	public String getLocalClusterId() {
		return relayContext.getClusterId();
	}

	public long getLocalInstanceId() {
		return relayContext.getInstanceId();
	}

	@Override
	public LocalServeClusterContext getContext() {
		return this.clusterContext;
	}

	public void heartbeat() {
		for (LocalServeInstance instance : this.getLocalInstances()) {
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
