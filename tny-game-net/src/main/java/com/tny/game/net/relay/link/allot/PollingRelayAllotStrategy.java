/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.net.relay.link.allot;

import com.tny.game.net.clusters.*;
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
        // int 计数溢出转负后 % 为负下标：floorMod 恒落 [0, size)
        return values.get(Math.floorMod(counter.incrementAndGet(), size));
    }

    @Override
    public ClientRelayLink allot(Tunnel tunnel, RelayServeInstance instance) {
        return random(instance.getActiveRelayLinks(), linkCounter);
    }

    @Override
    public RelayServeInstance allot(Tunnel tunnel, NetRemoteServeCluster cluster) {
        return random(cluster.getHealthyLocalInstances(), instanceCounter);
    }

}
