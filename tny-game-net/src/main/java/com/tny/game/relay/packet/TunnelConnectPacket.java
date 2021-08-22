package com.tny.game.relay.packet;

import com.tny.game.relay.packet.arguments.*;
import com.tny.game.relay.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TunnelConnectPacket extends BaseRelayPacket<TunnelConnectArguments> {

	public static final RelayPacketFactory<TunnelConnectPacket, TunnelConnectArguments> FACTORY = TunnelConnectPacket::new;

	public TunnelConnectPacket(long tunnelId, TunnelConnectArguments arguments) {
		super(RelayPacketType.TUNNEL_CONNECT, tunnelId, arguments);
	}

	public TunnelConnectPacket(long tunnelId, TunnelConnectArguments arguments, long time) {
		super(RelayPacketType.TUNNEL_CONNECT, tunnelId, time, arguments);
	}

	public TunnelConnectPacket(RelayTunnel<?> tunnel, TunnelConnectArguments arguments) {
		super(RelayPacketType.TUNNEL_CONNECT, tunnel, arguments);
	}

}
