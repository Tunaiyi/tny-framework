package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TunnelConnectPacket extends BaseTunnelPacket<TunnelConnectArguments> {

	public static final RelayPacketFactory<TunnelConnectPacket, TunnelConnectArguments> FACTORY = TunnelConnectPacket::new;

	public TunnelConnectPacket(int id, TunnelConnectArguments arguments) {
		super(id, RelayPacketType.TUNNEL_CONNECT, arguments);
	}

	public TunnelConnectPacket(int id, TunnelConnectArguments arguments, long time) {
		super(id, RelayPacketType.TUNNEL_CONNECT, time, arguments);
	}

}
