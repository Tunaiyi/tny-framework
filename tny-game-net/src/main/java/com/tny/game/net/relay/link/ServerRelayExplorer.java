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

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.application.*;

/**
 * 远程(客户端连接不在本地)转发服务
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 7:31 下午
 */
@UnitInterface
public interface ServerRelayExplorer extends RelayExplorer {

    /**
     * 获取指定的 tunnel
     *
     * @param instanceId 创建 tunnel 的服务实例 id
     * @param tunnelId   管道 id
     */
    @Override
    ServerRelayTunnel getTunnel(long instanceId, long tunnelId);

    /**
     * 接收打开的 link
     *
     * @param transport 转发器
     * @param service     集群 id
     * @param instance    实例 id
     */
    RelayLink acceptOpenLink(RelayTransport transport, RpcServiceType serviceType, String service, long instance, String key);

    /**
     * 接收连接的 Tunnel
     *
     * @param link           关联的 link
     * @param networkContext 网络上下文
     * @param instanceId     服务实例 id
     * @param tunnelId       管道 id
     * @param ip             远程ip
     * @param port           远程端口
     */
    void acceptConnectTunnel(NetRelayLink link, NetworkContext networkContext, long instanceId, long tunnelId, String ip, int port);

    /**
     * 切换 tunnel link
     *
     * @param link       转发连接
     * @param instanceId tunnel的服务实例 id
     * @param tunnelId   tunnel id
     */
    void switchTunnelLink(NetRelayLink link, long instanceId, long tunnelId);

}
