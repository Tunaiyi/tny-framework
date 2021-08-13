package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TubuleConnectPacket extends BaseRelayPacket<TubuleConnectArguments> {

	public static final RelayPacketFactory<TubuleConnectPacket, TubuleConnectArguments> FACTORY = TubuleConnectPacket::new;

	public TubuleConnectPacket(long tunnelId, TubuleConnectArguments arguments) {
		super(RelayPacketType.TUBULE_CONNECT, tunnelId, arguments);
	}

	public TubuleConnectPacket(long tunnelId, TubuleConnectArguments arguments, long time) {
		super(RelayPacketType.TUBULE_CONNECT, tunnelId, time, arguments);
	}

	public TubuleConnectPacket(RelayTubule<?> tubule, TubuleConnectArguments arguments) {
		super(RelayPacketType.TUBULE_CONNECT, tubule, arguments);
	}

}
