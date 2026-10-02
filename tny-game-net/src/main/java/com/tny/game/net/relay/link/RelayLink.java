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

import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

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
public interface RelayLink extends Connection, NetContact {

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
     * 关闭
     */
    void disconnect();

}
