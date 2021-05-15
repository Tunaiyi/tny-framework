package com.tny.game.net.passthrough;

import com.tny.game.net.transport.*;

/**
 * Client -> Gateway -> GameServer
 * <p>
 * -----------------------------------------------------------------------------------------------------
 * |    Client    |               Gateway           |             |            GameServer               |
 * |----------------------------------------------------------------------------------------------------|
 * |ClientTunnel1 | -> ServerTunnel1 ->             |             |                   GameServerTubule1 |
 * |ClientTunnel2 | -> ServerTunnel2 -> GatewayPipe | = Socket => | GameServerPipe -> GameServerTubule2 |
 * |ClientTunnel3 | -> ServerTunnel3 ->             |             |                   GameServerTubule3 |
 * -----------------------------------------------------------------------------------------------------
 * <p>
 * 使用 Gateway 架构时候, Pipe 代表 Gateway 到实际服务器的连接.
 * Pipe 管理着多个, 每个Tubule代表某一连接到 Gateway 的 Client 连接
 *
 *
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/1 9:06 下午
 */
public interface Tubule<UID> extends Tunnel<UID> {

}
