package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TubuleDisconnectedPacket extends BaseRelayPacket<VoidPacketArguments> {

	public static final RelayPacketFactory<TubuleDisconnectedPacket, VoidPacketArguments> FACTORY = TubuleDisconnectedPacket::new;

	public TubuleDisconnectedPacket(long tunnelId) {
		super(RelayPacketType.TUBULE_DISCONNECTED, tunnelId, VoidPacketArguments.of());
	}

	public TubuleDisconnectedPacket(long tunnelId, long time) {
		super(RelayPacketType.TUBULE_DISCONNECTED, tunnelId, time, VoidPacketArguments.of());
	}

	public TubuleDisconnectedPacket(long tunnelId, VoidPacketArguments arguments, long time) {
		super(RelayPacketType.TUBULE_DISCONNECTED, tunnelId, time, arguments);
	}

	public TubuleDisconnectedPacket(RelayTubule<?> tubule, long time) {
		super(RelayPacketType.TUBULE_DISCONNECTED, tubule, time, VoidPacketArguments.of());
	}

	public TubuleDisconnectedPacket(RelayTubule<?> tubule) {
		super(RelayPacketType.TUBULE_DISCONNECTED, tubule, VoidPacketArguments.of());
	}

}
