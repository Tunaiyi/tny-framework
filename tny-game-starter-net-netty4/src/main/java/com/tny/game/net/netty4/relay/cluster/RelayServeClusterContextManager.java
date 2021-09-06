package com.tny.game.net.netty4.relay.cluster;

import com.tny.game.net.relay.link.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 7:27 下午
 */
public class RelayServeClusterContextManager {

	private List<LocalServeClusterContext> clusterContexts;

	public RelayServeClusterContextManager(List<LocalServeClusterContext> clusterContexts) {
		this.clusterContexts = clusterContexts;
	}

	public List<LocalServeClusterContext> getClusterContexts() {
		return clusterContexts;
	}

	public RelayServeClusterContextManager setClusterContexts(List<LocalServeClusterContext> clusterContexts) {
		this.clusterContexts = clusterContexts;
		return this;
	}

}
