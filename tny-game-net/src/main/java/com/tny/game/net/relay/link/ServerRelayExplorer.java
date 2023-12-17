/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.relay.link;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.base.*;

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
     * @param transporter 转发器
     * @param service     集群 id
     * @param instance    实例 id
     */
    RelayLink acceptOpenLink(RelayTransporter transporter, RpcServiceType serviceType, String service, long instance, String key);

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
