package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TunnelDisconnectPacket extends BaseTunnelPacket<TunnelVoidArguments> {

	public static final RelayPacketFactory<TunnelDisconnectPacket, TunnelVoidArguments> FACTORY = TunnelDisconnectPacket::new;

	public TunnelDisconnectPacket(int id, long instanceId, long tunnelId) {
		super(id, RelayPacketType.TUNNEL_DISCONNECT, new TunnelVoidArguments(instanceId, tunnelId));
	}

	public TunnelDisconnectPacket(int id, long instanceId, long tunnelId, long nanoTime) {
		super(id, RelayPacketType.TUNNEL_DISCONNECT, nanoTime, new TunnelVoidArguments(instanceId, tunnelId));
	}

	public TunnelDisconnectPacket(int id, TunnelVoidArguments arguments, long time) {
		super(id, RelayPacketType.TUNNEL_DISCONNECT, time, arguments);
	}

}
