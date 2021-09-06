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
public class FixedRelayMessageRouter implements RelayMessageRouter {

	private final String clusterId;

	public FixedRelayMessageRouter(String clusterId) {
		this.clusterId = clusterId;
	}

	@Override
	public String route(LocalRelayTunnel<?> tunnel, MessageSchema schema) {
		return clusterId;
	}

}
