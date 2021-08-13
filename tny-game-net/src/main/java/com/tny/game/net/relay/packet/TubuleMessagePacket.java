package com.tny.game.net.relay.packet;

import com.tny.game.net.message.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.packet.arguments.*;

/**
 * 传输事件
 * <p>
 *
 * @author : kgtny
 * @date : 2021/2/26 5:14 上午
 */
public class TubuleMessagePacket extends BaseRelayPacket<TubuleMessageArguments> {

	public static final RelayPacketFactory<TubuleMessagePacket, TubuleMessageArguments> FACTORY = TubuleMessagePacket::new;

	public TubuleMessagePacket(long id, Message message) {
		super(RelayPacketType.TUBULE_MESSAGE, id, new TubuleMessageArguments(message));
	}

	public TubuleMessagePacket(long id, Message message, long time) {
		super(RelayPacketType.TUBULE_MESSAGE, id, time, new TubuleMessageArguments(message));
	}

	public TubuleMessagePacket(long id, TubuleMessageArguments arguments, long nanoTime) {
		super(RelayPacketType.TUBULE_MESSAGE, id, nanoTime, arguments);
	}

	public TubuleMessagePacket(RelayTubule<?> tubule, Message message) {
		super(RelayPacketType.TUBULE_MESSAGE, tubule, new TubuleMessageArguments(message));
	}

	public TubuleMessagePacket(RelayTubule<?> tubule, Message message, long time) {
		super(RelayPacketType.TUBULE_MESSAGE, tubule, time, new TubuleMessageArguments(message));
	}

}