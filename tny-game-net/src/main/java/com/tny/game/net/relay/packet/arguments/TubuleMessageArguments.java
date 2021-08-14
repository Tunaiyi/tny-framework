package com.tny.game.net.relay.packet.arguments;

import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/10 12:05 下午
 */
public class TubuleMessageArguments implements RelayPacketArguments {

	private final Message message;

	public TubuleMessageArguments(Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return this.message;
	}

}
