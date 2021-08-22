package com.tny.game.relay.packet;

import com.tny.game.relay.packet.arguments.*;
import com.tny.game.relay.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TunnelConnectedPacket extends BaseRelayPacket<TunnelConnectedArguments> {

	public static final RelayPacketFactory<TunnelConnectedPacket, TunnelConnectedArguments> FACTORY = TunnelConnectedPacket::new;

	public TunnelConnectedPacket(long tunnelId, boolean result) {
		super(RelayPacketType.TUNNEL_CONNECTED, tunnelId, TunnelConnectedArguments.of(result));
	}

	public TunnelConnectedPacket(long tunnelId, boolean result, long time) {
		super(RelayPacketType.TUNNEL_CONNECTED, tunnelId, time, TunnelConnectedArguments.of(result));
	}

	public TunnelConnectedPacket(long tunnelId, TunnelConnectedArguments arguments, long time) {
		super(RelayPacketType.TUNNEL_CONNECTED, tunnelId, time, arguments);
	}

	public TunnelConnectedPacket(RelayTunnel<?> tunnel, boolean result, long time) {
		super(RelayPacketType.TUNNEL_CONNECTED, tunnel, time, TunnelConnectedArguments.of(result));
	}

	public TunnelConnectedPacket(RelayTunnel<?> tunnel, boolean result) {
		super(RelayPacketType.TUNNEL_CONNECTED, tunnel, TunnelConnectedArguments.of(result));
	}

}
