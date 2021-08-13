package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 8:52 下午
 */
public class PipeClosePacket extends BaseRelayPacket<VoidPacketArguments> {

	public static final RelayPacketFactory<PipeClosePacket, VoidPacketArguments> FACTORY = PipeClosePacket::new;

	public PipeClosePacket(long tunnelId) {
		super(RelayPacketType.PIPE_CLOSE, tunnelId, VoidPacketArguments.of());
	}

	public PipeClosePacket(long tunnelId, long time) {
		super(RelayPacketType.PIPE_CLOSE, tunnelId, time, VoidPacketArguments.of());
	}

	public PipeClosePacket(long tunnelId, VoidPacketArguments arguments, long time) {
		super(RelayPacketType.PIPE_CLOSE, tunnelId, time, arguments);
	}

	public PipeClosePacket(RelayTubule<?> tubule, long time) {
		super(RelayPacketType.PIPE_CLOSE, tubule, time, VoidPacketArguments.of());
	}

	public PipeClosePacket(RelayTubule<?> tubule) {
		super(RelayPacketType.PIPE_CLOSE, tubule, VoidPacketArguments.of());
	}

}
