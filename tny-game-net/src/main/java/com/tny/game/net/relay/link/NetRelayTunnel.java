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

import com.tny.game.net.transport.*;

/**
 * 转发通道
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
 * Link 管理着多个, 每个Repeater代表某一连接到 Gateway 的 Client 连接
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/3 11:46 上午
 */
public interface NetRelayTunnel extends NetTunnel, RelayTunnel {

    //	/**
    //	 * 如果当前 link 是指定 link 对象的话, 关闭 tunnel
    //	 *
    //	 * @param link 发送关闭的 link
    //	 */
    //	void onLinkDisconnect(NetRelayLink link);

}
