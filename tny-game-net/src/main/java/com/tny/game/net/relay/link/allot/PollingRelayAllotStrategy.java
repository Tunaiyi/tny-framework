/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.relay.link.allot;

import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询分配策略
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/2 1:54 下午
 */
public class PollingRelayAllotStrategy implements RelayLinkAllotStrategy, ServeInstanceAllotStrategy {

    private final AtomicInteger instanceCounter = new AtomicInteger();

    private final AtomicInteger linkCounter = new AtomicInteger();

    private <T> T random(List<T> values, AtomicInteger counter) {
        int size = values.size();
        if (size == 0) {
            return null;
        }
        if (size == 1) {
            return values.get(0);
        }
        return values.get(counter.incrementAndGet() % size);
    }

    @Override
    public RemoteRelayLink allot(Tunnel<?> tunnel, RemoteServeInstance instance) {
        return random(instance.getActiveRelayLinks(), linkCounter);
    }

    @Override
    public RemoteServeInstance allot(Tunnel<?> tunnel, NetRemoteServeCluster cluster) {
        return random(cluster.getHealthyLocalInstances(), instanceCounter);
    }

}
