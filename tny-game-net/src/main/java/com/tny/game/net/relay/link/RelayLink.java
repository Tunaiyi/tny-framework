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

import com.tny.game.net.rpc.*;

import java.net.InetSocketAddress;

/**
 * Client -> Gateway -> GameServer
 * <p>
 * -------------------------------------------------------------------------------------------------------------------------------------------
 * |    Client    |             |               Gateway                                 |             |            GameServer                |
 * |---------------------------------------------------------------------------------------------------------------------------------------- |
 * |ClientTunnel1 |             | -> ServerTunnel1 -> LocalAccessTunnel1 ->             |             |                   RemoteRelayTunnel1 |
 * |ClientTunnel2 | = Socket => | -> ServerTunnel2 -> LocalAccessTunnel2 -> GatewayLink | = Socket => | GameServerLink -> RemoteRelayTunnel2 |
 * |ClientTunnel3 |             | -> ServerTunnel3 -> LocalAccessTunnel3 ->             |             |                   RemoteRelayTunnel3 |
 * -------------------------------------------------------------------------------------------------------------------------------------------
 * <p>
 * 使用 Gateway 架构时候, Link 代表 Gateway 到实际服务器的连接.
 * Link 管理着多个Tunnel, 每个Tunnel代表某一Client
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/1 9:06 下午
 */
public interface RelayLink {

    /**
     * @return 线路 唯一 Id
     */
    String getId();

    /**
     * @return link 唯一 key
     */
    String getKey();

    /**
     * @return 发现服务器服务名
     */
    String getService();

    /**
     * @return 节点 id
     */
    long getInstanceId();

    /**
     * @return 创建时间
     */
    long getCreateTime();

    /**
     * @return 获取 Tunnel 状态
     */
    RelayLinkStatus getStatus();

    /**
     * @return 返回远程地址
     */
    InetSocketAddress getRemoteAddress();

    /**
     * @return 返回本地地址
     */
    InetSocketAddress getLocalAddress();

    /**
     * @return 接入模式
     */
    NetAccessMode getAccessMode();

    /**
     * 关闭
     */
    void close();

    /**
     * 关闭
     */
    void disconnect();

    /**
     * 是否活跃
     */
    boolean isActive();

}
