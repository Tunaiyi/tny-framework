package com.tny.game.net.relay.link.allot;

import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 1:54 下午
 */
public class RandomRelayAllotStrategy implements LocalRelayLinkAllotStrategy, LocalServeInstanceAllotStrategy {

	private <T> T random(List<T> values) {
		int size = values.size();
		if (size == 0) {
			return null;
		}
		if (size == 1) {
			return values.get(0);
		}
		return values.get(ThreadLocalRandom.current().nextInt() % size);
	}

	@Override
	public LocalRelayLink allot(NetTunnel<?> tunnel, LocalServeInstance instance) {
		return random(instance.getRelayLinks());
	}

	@Override
	public LocalServeInstance allot(NetTunnel<?> tunnel, LocalServeCluster cluster) {
		return random(cluster.getLocalInstances());
	}

}
