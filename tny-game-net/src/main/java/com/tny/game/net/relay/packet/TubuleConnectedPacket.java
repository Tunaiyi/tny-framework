package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TubuleConnectedPacket extends BaseRelayPacket<VoidPacketArguments> {

	public static final RelayPacketFactory<TubuleConnectedPacket, VoidPacketArguments> FACTORY = TubuleConnectedPacket::new;

	public TubuleConnectedPacket(long tunnelId) {
		super(RelayPacketType.TUBULE_CONNECTED, tunnelId, VoidPacketArguments.of());
	}

	public TubuleConnectedPacket(long tunnelId, long time) {
		super(RelayPacketType.TUBULE_CONNECTED, tunnelId, time, VoidPacketArguments.of());
	}

	public TubuleConnectedPacket(long tunnelId, VoidPacketArguments arguments, long time) {
		super(RelayPacketType.TUBULE_CONNECTED, tunnelId, time, arguments);
	}

	public TubuleConnectedPacket(RelayTubule<?> tubule, long time) {
		super(RelayPacketType.TUBULE_CONNECTED, tubule, time, VoidPacketArguments.of());
	}

	public TubuleConnectedPacket(RelayTubule<?> tubule) {
		super(RelayPacketType.TUBULE_CONNECTED, tubule, VoidPacketArguments.of());
	}

}
