package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.packet.arguments.*;

/**
 * 事件
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/4 8:23 下午
 */
public abstract class BaseTunnelPacket<A extends TunnelPacketArguments> extends BaseRelayPacket<A> {

	public BaseTunnelPacket(int id, RelayPacketType type, A arguments) {
		super(id, type, arguments);
	}

	public BaseTunnelPacket(int id, RelayPacketType type, long time, A arguments) {
		super(id, type, time, arguments);
	}

}
