package com.tny.game.relay.packet;

import com.tny.game.relay.packet.arguments.*;
import com.tny.game.relay.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TunnelDisconnectedPacket extends BaseRelayPacket<VoidPacketArguments> {

	public static final RelayPacketFactory<TunnelDisconnectedPacket, VoidPacketArguments> FACTORY = TunnelDisconnectedPacket::new;

	public TunnelDisconnectedPacket(long tunnelId) {
		super(RelayPacketType.TUNNEL_DISCONNECTED, tunnelId, VoidPacketArguments.of());
	}

	public TunnelDisconnectedPacket(long tunnelId, long time) {
		super(RelayPacketType.TUNNEL_DISCONNECTED, tunnelId, time, VoidPacketArguments.of());
	}

	public TunnelDisconnectedPacket(long tunnelId, VoidPacketArguments arguments, long time) {
		super(RelayPacketType.TUNNEL_DISCONNECTED, tunnelId, time, arguments);
	}

	public TunnelDisconnectedPacket(RelayTunnel<?> tunnel, long time) {
		super(RelayPacketType.TUNNEL_DISCONNECTED, tunnel, time, VoidPacketArguments.of());
	}

	public TunnelDisconnectedPacket(RelayTunnel<?> tunnel) {
		super(RelayPacketType.TUNNEL_DISCONNECTED, tunnel, VoidPacketArguments.of());
	}

}
