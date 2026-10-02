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
