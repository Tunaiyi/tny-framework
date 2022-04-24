package com.tny.game.net.relay.link.allot;

import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机分配策略
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 1:54 下午
 */
public class RandomRelayAllotStrategy implements RelayLinkAllotStrategy, ServeInstanceAllotStrategy {

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
    public RemoteRelayLink allot(Tunnel<?> tunnel, RemoteServeInstance instance) {
        return random(instance.getActiveRelayLinks());
    }

    @Override
    public RemoteServeInstance allot(Tunnel<?> tunnel, NetRemoteServeCluster cluster) {
        return random(cluster.getHealthyLocalInstances());
    }

}
