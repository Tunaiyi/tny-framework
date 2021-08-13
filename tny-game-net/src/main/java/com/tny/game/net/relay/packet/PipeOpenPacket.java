package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.packet.arguments.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/8/9 8:52 下午
 */
public class PipeOpenPacket extends BaseRelayPacket<VoidPacketArguments> {

	public static final RelayPacketFactory<PipeOpenPacket, VoidPacketArguments> FACTORY = PipeOpenPacket::new;

	public PipeOpenPacket(long tunnelId) {
		super(RelayPacketType.PIPE_OPEN, tunnelId, VoidPacketArguments.of());
	}

	public PipeOpenPacket(long tunnelId, long time) {
		super(RelayPacketType.PIPE_OPEN, tunnelId, time, VoidPacketArguments.of());
	}

	public PipeOpenPacket(long tunnelId, VoidPacketArguments arguments, long time) {
		super(RelayPacketType.PIPE_OPEN, tunnelId, time, arguments);
	}

}
