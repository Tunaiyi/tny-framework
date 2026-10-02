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
package com.tny.game.net.relay.link;

import com.tny.game.net.application.*;
import com.tny.game.net.relay.link.route.*;

/**
 * 本地转发服务上下问
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/30 2:47 下午
 */
public interface ClientRelayContext {

    /**
     * @return 获取当前服务实例 id
     */
    long getInstanceId();

    /**
     * @return 获取当前服务实例 id
     */
    String getService();

    /**
     * @return 分配 link id
     */
    String createLinkKey(String service);

    /**
     * @return 获取 message 路由器
     */
    RelayMessageRouter getRelayMessageRouter();

    /**
     * @return 获取 ServeCluster 过滤器
     */
    ServeClusterFilter getServeClusterFilter();

    /**
     * @return 获取网络应用上下问
     */
    NetAppContext getAppContext();

}
