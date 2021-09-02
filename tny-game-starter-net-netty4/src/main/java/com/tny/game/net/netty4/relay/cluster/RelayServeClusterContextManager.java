package com.tny.game.net.netty4.relay.cluster;

import com.tny.game.net.netty4.relay.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 7:27 下午
 */
public class RelayServeClusterContextManager {

	private List<RelayServeClusterContext> clusterContexts;

	public RelayServeClusterContextManager(List<RelayServeClusterContext> clusterContexts) {
		this.clusterContexts = clusterContexts;
	}

	public List<RelayServeClusterContext> getClusterContexts() {
		return clusterContexts;
	}

	public RelayServeClusterContextManager setClusterContexts(List<RelayServeClusterContext> clusterContexts) {
		this.clusterContexts = clusterContexts;
		return this;
	}

}
