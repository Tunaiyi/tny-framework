package com.tny.game.net.relay.link.route;

import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.*;

/**
 * 转发消息路由器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 7:42 下午
 */
public interface RelayMessageRouter {

	/**
	 * 消息路由转发连接
	 *
	 * @param tunnel 发送的管道
	 * @param schema 消息
	 * @return 返回转发的连接
	 */
	String route(RemoteRelayTunnel<?> tunnel, MessageSchema schema);

}
