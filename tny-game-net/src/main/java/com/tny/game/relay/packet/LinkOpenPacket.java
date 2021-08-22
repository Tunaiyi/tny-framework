package com.tny.game.relay.packet;

import com.tny.game.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 8:52 下午
 */
public class LinkOpenPacket extends BaseRelayPacket<VoidPacketArguments> {

	public static final RelayPacketFactory<LinkOpenPacket, VoidPacketArguments> FACTORY = LinkOpenPacket::new;

	public LinkOpenPacket(long tunnelId) {
		super(RelayPacketType.LINK_OPEN, tunnelId, VoidPacketArguments.of());
	}

	public LinkOpenPacket(long tunnelId, long time) {
		super(RelayPacketType.LINK_OPEN, tunnelId, time, VoidPacketArguments.of());
	}

	public LinkOpenPacket(long tunnelId, VoidPacketArguments arguments, long time) {
		super(RelayPacketType.LINK_OPEN, tunnelId, time, arguments);
	}

}
