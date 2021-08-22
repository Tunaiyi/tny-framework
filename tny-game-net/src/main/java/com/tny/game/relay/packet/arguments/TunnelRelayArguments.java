package com.tny.game.relay.packet.arguments;

import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 12:05 下午
 */
public class TunnelRelayArguments implements RelayPacketArguments {

	private final Message message;

	public TunnelRelayArguments(Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return this.message;
	}

	@Override
	public void release() {
		Object body = message.getBody();
		if (body instanceof OctetMessageBody) {
			((OctetMessageBody)body).release();
		}
	}

}
