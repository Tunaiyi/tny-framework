package com.tny.game.relay.packet;

import com.tny.game.relay.packet.arguments.*;
import com.tny.game.relay.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 8:52 下午
 */
public class LinkClosePacket extends BaseRelayPacket<VoidPacketArguments> {

	public static final RelayPacketFactory<LinkClosePacket, VoidPacketArguments> FACTORY = LinkClosePacket::new;

	public LinkClosePacket(long tunnelId) {
		super(RelayPacketType.LINK_CLOSE, tunnelId, VoidPacketArguments.of());
	}

	public LinkClosePacket(long tunnelId, long time) {
		super(RelayPacketType.LINK_CLOSE, tunnelId, time, VoidPacketArguments.of());
	}

	public LinkClosePacket(long tunnelId, VoidPacketArguments arguments, long time) {
		super(RelayPacketType.LINK_CLOSE, tunnelId, time, arguments);
	}

	public LinkClosePacket(RelayTunnel<?> tunnel, long time) {
		super(RelayPacketType.LINK_CLOSE, tunnel, time, VoidPacketArguments.of());
	}

	public LinkClosePacket(RelayTunnel<?> tunnel) {
		super(RelayPacketType.LINK_CLOSE, tunnel, VoidPacketArguments.of());
	}

}
