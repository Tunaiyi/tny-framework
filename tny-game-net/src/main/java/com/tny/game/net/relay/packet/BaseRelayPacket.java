package com.tny.game.net.relay.packet;

import com.tny.game.net.relay.*;
import com.tny.game.net.relay.packet.arguments.*;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 事件
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/4 8:23 下午
 */
public abstract class BaseRelayPacket<A extends RelayPacketArguments> implements RelayPacket<A> {

	private final long time;

	private final long tunnelId;

	private final RelayPacketType type;

	private final A arguments;

	public BaseRelayPacket(RelayPacketType type, long tunnelId, A arguments) {
		this.type = type;
		this.tunnelId = tunnelId;
		this.arguments = arguments;
		this.time = System.currentTimeMillis();
	}

	public BaseRelayPacket(RelayPacketType type, long tunnelId, long time, A arguments) {
		this.type = type;
		this.tunnelId = tunnelId;
		this.arguments = arguments;
		this.time = time;
	}

	public BaseRelayPacket(RelayPacketType type, RelayTubule<?> tubule, long time, A arguments) {
		this(type, tubule.getId(), time, arguments);
	}

	public BaseRelayPacket(RelayPacketType type, RelayTubule<?> tubule, A arguments) {
		this(type, tubule.getId(), arguments);
	}

	@Override
	public RelayPacketType getType() {
		return type;
	}

	@Override
	public long getTunnelId() {
		return this.tunnelId;
	}

	@Override
	public long getTime() {
		return this.time;
	}

	@Override
	public A getArguments() {
		return arguments;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("nanoTime", this.time)
				.append("tunnelId", this.tunnelId)
				.toString();
	}

}
