package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 8:52 下午
 */
public class LinkOpenedPacket extends BaseLinkPacket<LinkOpenedArguments> {

	public static final RelayPacketFactory<LinkOpenedPacket, LinkOpenedArguments> FACTORY = LinkOpenedPacket::new;

	public LinkOpenedPacket(int id, boolean result) {
		super(id, RelayPacketType.LINK_OPENED, LinkOpenedArguments.of(result));
	}

	public LinkOpenedPacket(int id, LinkOpenedArguments arguments, long time) {
		super(id, RelayPacketType.LINK_OPENED, time, arguments);
	}

}
