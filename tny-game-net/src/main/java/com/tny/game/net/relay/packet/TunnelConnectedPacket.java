package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TunnelConnectedPacket extends BaseTunnelPacket<TunnelConnectedArguments> {

	public static final RelayPacketFactory<TunnelConnectedPacket, TunnelConnectedArguments> FACTORY = TunnelConnectedPacket::new;

	public TunnelConnectedPacket(int id, long tunnelId, boolean result) {
		super(id, RelayPacketType.TUNNEL_CONNECTED, TunnelConnectedArguments.ofResult(tunnelId, result));
	}

	public TunnelConnectedPacket(int id, TunnelConnectedArguments arguments) {
		super(id, RelayPacketType.TUNNEL_CONNECTED, arguments);
	}

	public TunnelConnectedPacket(int id, TunnelConnectedArguments arguments, long time) {
		super(id, RelayPacketType.TUNNEL_CONNECTED, time, arguments);
	}

}
