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
public interface NetRelayTunnel<UID> extends NetTunnel<UID>, RelayTunnel<UID> {

	/**
	 * link 转发连接发送关闭请求
	 *
	 * @param link 发送关闭的 link
	 */
	void closeOnLink(NetRelayLink link);

}
