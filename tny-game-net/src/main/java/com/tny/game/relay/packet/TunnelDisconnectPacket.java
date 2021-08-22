package com.tny.game.relay.packet;

import com.tny.game.relay.packet.arguments.*;
import com.tny.game.relay.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TunnelDisconnectPacket extends BaseRelayPacket<VoidPacketArguments> {

	public static final RelayPacketFactory<TunnelDisconnectPacket, VoidPacketArguments> FACTORY = TunnelDisconnectPacket::new;

	public TunnelDisconnectPacket(long tunnelId) {
		super(RelayPacketType.TUNNEL_DISCONNECT, tunnelId, VoidPacketArguments.of());
	}

	public TunnelDisconnectPacket(long tunnelId, long nanoTime) {
		super(RelayPacketType.TUNNEL_DISCONNECT, tunnelId, nanoTime, VoidPacketArguments.of());
	}

	public TunnelDisconnectPacket(long tunnelId, VoidPacketArguments arguments, long time) {
		super(RelayPacketType.TUNNEL_DISCONNECT, tunnelId, time, arguments);
	}

	public TunnelDisconnectPacket(RelayTunnel<?> tunnel, long nanoTime) {
		super(RelayPacketType.TUNNEL_DISCONNECT, tunnel, nanoTime, VoidPacketArguments.of());
	}

	public TunnelDisconnectPacket(RelayTunnel<?> tunnel) {
		super(RelayPacketType.TUNNEL_DISCONNECT, tunnel, VoidPacketArguments.of());
	}

}
