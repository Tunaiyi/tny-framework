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
        // 全值域 nextInt() 约半数取负，%size 后为负下标——必须用有界重载
        return values.get(ThreadLocalRandom.current().nextInt(size));
    }

    @Override
    public ClientRelayLink allot(Tunnel tunnel, RelayServeInstance instance) {
        return random(instance.getActiveRelayLinks());
    }

    @Override
    public RelayServeInstance allot(Tunnel tunnel, NetRemoteServeCluster cluster) {
        return random(cluster.getHealthyLocalInstances());
    }

}
