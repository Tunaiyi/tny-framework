package com.tny.game.net.relay.cluster;

import com.google.common.collect.ImmutableList;
import com.tny.game.net.relay.cluster.watch.*;

import java.util.List;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/13 4:45 下午
 */
public class NoopServeNodeClient implements ServeNodeClient {

	@Override
	public List<ServeNode> getAllServeNodes(String serveName) {
		return ImmutableList.of();
	}

	@Override
	public List<ServeNode> getHealthyServeNodes(String serveName) {
		return ImmutableList.of();
	}

	@Override
	public ServeNode getServeNode(String serveName, long id) {
		return null;
	}

	@Override
	public ServeNode getHealthyServeNode(String serveName, long id) {
		return null;
	}

	@Override
	public void subscribe(String serveName, ServeNodeListener listener) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unsubscribe(String serveName, ServeNodeListener listener) {
		throw new UnsupportedOperationException();
	}

}
