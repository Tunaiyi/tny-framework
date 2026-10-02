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
import com.tny.game.net.transport.*;

/**
 * 转发目标服务实例分配策略
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 8:11 下午
 */
public interface ServeInstanceAllotStrategy {

    /**
     * 分配服务实例
     *
     * @param tunnel  分配的通讯管道
     * @param cluster 目标集群
     * @return 返回服务实例
     */
    RelayServeInstance allot(Tunnel tunnel, NetRemoteServeCluster cluster);

}