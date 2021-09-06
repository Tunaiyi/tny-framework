package com.tny.game.net.relay.packet;

import com.tny.game.net.message.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * 传输事件
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TunnelRelayPacket extends BaseTunnelPacket<TunnelRelayArguments> {

	public static final RelayPacketFactory<TunnelRelayPacket, TunnelRelayArguments> FACTORY = TunnelRelayPacket::new;

	public TunnelRelayPacket(int id, long instanceId, long tunnelId, Message message) {
		super(id, RelayPacketType.TUNNEL_RELAY, new TunnelRelayArguments(instanceId, tunnelId, message));
	}

	public TunnelRelayPacket(int id, long instanceId, long tunnelId, Message message, long time) {
		super(id, RelayPacketType.TUNNEL_RELAY, time, new TunnelRelayArguments(instanceId, tunnelId, message));
	}

	public TunnelRelayPacket(int id, TunnelRelayArguments arguments, long nanoTime) {
		super(id, RelayPacketType.TUNNEL_RELAY, nanoTime, arguments);
	}

}