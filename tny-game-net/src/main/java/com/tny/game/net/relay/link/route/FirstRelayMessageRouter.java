package com.tny.game.net.relay.link.route;

import com.tny.game.net.message.*;
import com.tny.game.net.relay.link.*;
import com.tny.game.net.transport.*;

import java.util.Map;

/**
 * 转发消息路由器
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/25 7:42 下午
 */
public class FirstRelayMessageRouter implements RelayMessageRouter {

	@Override
	public LocalRelayLink route(Tunnel<?> tunnel, MessageSchema schema, Map<String, TunnelRelayLinker> map) {
		for (TunnelRelayLinker linker : map.values())
			return linker.link();
		return null;
	}

}
