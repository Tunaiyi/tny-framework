package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.*;
import com.tny.game.net.relay.packet.arguments.*;

import static com.tny.game.net.relay.packet.RelayPacketType.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class PipeHeartBeatPacket extends BaseRelayPacket<VoidPacketArguments> {

	public static final RelayPacketFactory<PipeHeartBeatPacket, VoidPacketArguments> PING_FACTORY =
			(id, args, time) -> PipeHeartBeatPacket.ping(id, time);

	public static final RelayPacketFactory<PipeHeartBeatPacket, VoidPacketArguments> PONG_FACTORY =
			(id, args, time) -> PipeHeartBeatPacket.pong(id, time);

	public static PipeHeartBeatPacket ping(long tunnelId) {
		return new PipeHeartBeatPacket(PIPE_PING, tunnelId);
	}

	public static PipeHeartBeatPacket pong(long tunnelId) {
		return new PipeHeartBeatPacket(PIPE_PONG, tunnelId);
	}

	public static PipeHeartBeatPacket ping(long tunnelId, long time) {
		return new PipeHeartBeatPacket(PIPE_PING, tunnelId, time);
	}

	public static PipeHeartBeatPacket pong(long tunnelId, long time) {
		return new PipeHeartBeatPacket(PIPE_PONG, tunnelId, time);
	}

	public PipeHeartBeatPacket(RelayPacketType type, long tunnelId) {
		super(type, tunnelId, VoidPacketArguments.of());
	}

	public PipeHeartBeatPacket(RelayPacketType type, long tunnelId, long time) {
		super(type, tunnelId, time, VoidPacketArguments.of());
	}

	public PipeHeartBeatPacket(RelayPacketType type, RelayTubule<?> tubule, long time) {
		super(type, tubule, time, VoidPacketArguments.of());
	}

	public PipeHeartBeatPacket(RelayPacketType type, RelayTubule<?> tubule) {
		super(type, tubule, VoidPacketArguments.of());
	}

}
