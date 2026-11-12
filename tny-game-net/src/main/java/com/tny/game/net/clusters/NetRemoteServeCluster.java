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

package com.tny.game.net.clusters;

import com.tny.game.net.relay.cluster.*;
import com.tny.game.net.relay.link.*;

/**
 * 本地集群客户端管理
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/23 9:20 下午
 */
public interface NetRemoteServeCluster extends RemoteServeCluster {

    /**
     * 关闭本地集群连接
     */
    void close();

    /**
     * 注册 LocaleServeInstance, 如果存在返回旧的 instance
     *
     * @param instance 注册的 instance
     * @return 返回 instance
     */
    RelayServeInstance registerInstance(NetRelayServeInstance instance);

    /**
     * 刷新实例
     */
    void refreshInstances();

    /**
     * 卸载指定 instanceId 的 Instance
     *
     * @param instanceId 指定的Instance id
     */
    void unregisterInstance(long instanceId);

    /**
     * @param node 更新节点信息
     */
    void updateInstance(ServeNode node);

}
