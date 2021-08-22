package com.tny.game.relay.packet;

import com.tny.game.net.message.*;
import com.tny.game.relay.packet.arguments.*;
import com.tny.game.relay.transport.*;

/**
 * 传输事件
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TunnelRelayPacket extends BaseRelayPacket<TunnelRelayArguments> {

	public static final RelayPacketFactory<TunnelRelayPacket, TunnelRelayArguments> FACTORY = TunnelRelayPacket::new;

	public TunnelRelayPacket(long id, Message message) {
		super(RelayPacketType.TUNNEL_RELAY, id, new TunnelRelayArguments(message));
	}

	public TunnelRelayPacket(long id, Message message, long time) {
		super(RelayPacketType.TUNNEL_RELAY, id, time, new TunnelRelayArguments(message));
	}

	public TunnelRelayPacket(long id, TunnelRelayArguments arguments, long nanoTime) {
		super(RelayPacketType.TUNNEL_RELAY, id, nanoTime, arguments);
	}

	public TunnelRelayPacket(RelayTunnel<?> tunnel, Message message) {
		super(RelayPacketType.TUNNEL_RELAY, tunnel, new TunnelRelayArguments(message));
	}

	public TunnelRelayPacket(RelayTunnel<?> tunnel, Message message, long time) {
		super(RelayPacketType.TUNNEL_RELAY, tunnel, time, new TunnelRelayArguments(message));
	}

}