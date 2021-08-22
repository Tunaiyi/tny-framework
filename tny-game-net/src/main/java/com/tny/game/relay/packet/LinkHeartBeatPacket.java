package com.tny.game.relay.packet;

import com.tny.game.relay.packet.arguments.*;
import com.tny.game.relay.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class LinkHeartBeatPacket extends BaseRelayPacket<VoidPacketArguments> {

	public static final RelayPacketFactory<LinkHeartBeatPacket, VoidPacketArguments> PING_FACTORY =
			(id, args, time) -> LinkHeartBeatPacket.ping(id, time);

	public static final RelayPacketFactory<LinkHeartBeatPacket, VoidPacketArguments> PONG_FACTORY =
			(id, args, time) -> LinkHeartBeatPacket.pong(id, time);

	public static LinkHeartBeatPacket ping(long tunnelId) {
		return new LinkHeartBeatPacket(RelayPacketType.LINK_PING, tunnelId);
	}

	public static LinkHeartBeatPacket pong(long tunnelId) {
		return new LinkHeartBeatPacket(RelayPacketType.LINK_PONG, tunnelId);
	}

	public static LinkHeartBeatPacket ping(long tunnelId, long time) {
		return new LinkHeartBeatPacket(RelayPacketType.LINK_PING, tunnelId, time);
	}

	public static LinkHeartBeatPacket pong(long tunnelId, long time) {
		return new LinkHeartBeatPacket(RelayPacketType.LINK_PONG, tunnelId, time);
	}

	public LinkHeartBeatPacket(RelayPacketType type, long tunnelId) {
		super(type, tunnelId, VoidPacketArguments.of());
	}

	public LinkHeartBeatPacket(RelayPacketType type, long tunnelId, long time) {
		super(type, tunnelId, time, VoidPacketArguments.of());
	}

	public LinkHeartBeatPacket(RelayPacketType type, RelayTunnel<?> tunnel, long time) {
		super(type, tunnel, time, VoidPacketArguments.of());
	}

	public LinkHeartBeatPacket(RelayPacketType type, RelayTunnel<?> tunnel) {
		super(type, tunnel, VoidPacketArguments.of());
	}

}
