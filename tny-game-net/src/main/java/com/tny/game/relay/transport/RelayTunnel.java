package com.tny.game.relay.transport;

import com.tny.game.net.transport.*;

/**
 * 转发通道
 * Client -> Gateway -> GameServer
 * <p>
 * ---------------------------------------------------------------------------------------------------------------------------------------------
 * |    Client    |             |               Gateway                                 |             |            GameServer                  |
 * |------------------------------------------------------------------------------------------------------------------------------------------ |
 * |ClientTunnel1 |             | -> ServerTunnel1 -> RelayAccessTunnel1 ->             |             |                   RelayProviderTunnel1 |
 * |ClientTunnel2 | = Socket => | -> ServerTunnel2 -> RelayAccessTunnel2 -> GatewayPipe | = Socket => | GameServerPipe -> RelayProviderTunnel2 |
 * |ClientTunnel3 |             | -> ServerTunnel3 -> RelayAccessTunnel3 ->             |             |                   RelayProviderTunnel3 |
 * ---------------------------------------------------------------------------------------------------------------------------------------------
 * <p>
 * 使用 Gateway 架构时候, Pipe 代表 Gateway 到实际服务器的连接.
 * Pipe 管理着多个, 每个Repeater代表某一连接到 Gateway 的 Client 连接
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/3 11:46 上午
 */
public interface RelayTunnel<UID> extends NetTunnel<UID> {

}
