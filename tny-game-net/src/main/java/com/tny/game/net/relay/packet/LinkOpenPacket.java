package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 8:52 下午
 */
public class LinkOpenPacket extends BaseLinkPacket<LinkOpenArguments> {

	public static final RelayPacketFactory<LinkOpenPacket, LinkOpenArguments> FACTORY = LinkOpenPacket::new;

	public LinkOpenPacket(int id, LinkOpenArguments arguments) {
		super(id, RelayPacketType.LINK_OPENING, arguments);
	}

	public LinkOpenPacket(int id, LinkOpenArguments arguments, long time) {
		super(id, RelayPacketType.LINK_OPENING, time, arguments);
	}

}
