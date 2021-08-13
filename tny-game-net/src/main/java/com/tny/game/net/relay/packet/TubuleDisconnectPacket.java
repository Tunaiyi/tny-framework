package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TubuleDisconnectPacket extends BaseRelayPacket<VoidPacketArguments> {

	public static final RelayPacketFactory<TubuleDisconnectPacket, VoidPacketArguments> FACTORY = TubuleDisconnectPacket::new;

	public TubuleDisconnectPacket(long tunnelId) {
		super(RelayPacketType.TUBULE_DISCONNECT, tunnelId, VoidPacketArguments.of());
	}

	public TubuleDisconnectPacket(long tunnelId, long nanoTime) {
		super(RelayPacketType.TUBULE_DISCONNECT, tunnelId, nanoTime, VoidPacketArguments.of());
	}

	public TubuleDisconnectPacket(long tunnelId, VoidPacketArguments arguments, long time) {
		super(RelayPacketType.TUBULE_DISCONNECT, tunnelId, time, arguments);
	}

	public TubuleDisconnectPacket(RelayTubule<?> tubule, long nanoTime) {
		super(RelayPacketType.TUBULE_DISCONNECT, tubule, nanoTime, VoidPacketArguments.of());
	}

	public TubuleDisconnectPacket(RelayTubule<?> tubule) {
		super(RelayPacketType.TUBULE_DISCONNECT, tubule, VoidPacketArguments.of());
	}

}
