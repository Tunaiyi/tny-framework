package com.tny.game.net.relay.packet;

import com.tny.game.common.enums.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.exception.*;
import com.tny.game.net.relay.packet.arguments.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.relay.RelayCodecConstants.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/1 3:04 上午
 */
public enum RelayPacketType implements EnumIdentifiable<Byte>, RelayPackerHandlerInvoker<RelayPacket<?>> {

	PIPE_OPEN(RELAY_PACKET_TYPE_PIPE_OPEN,
			PipeOpenPacket.class, VoidPacketArguments.class,
			PipeOpenPacket.FACTORY,
			RelayPacketHandler::onPipeOpen),

	PIPE_CLOSE(RELAY_PACKET_TYPE_PIPE_CLOSE,
			PipeClosePacket.class, VoidPacketArguments.class,
			PipeClosePacket.FACTORY,
			RelayPacketHandler::onPipeClose),

	PIPE_PING(RELAY_PACKET_TYPE_PIPE_PING,
			PipeHeartBeatPacket.class, VoidPacketArguments.class,
			PipeHeartBeatPacket.PING_FACTORY,
			RelayPacketHandler::onPipeHeartBeat),

	PIPE_PONG(RELAY_PACKET_TYPE_PIPE_PONG,
			PipeHeartBeatPacket.class, VoidPacketArguments.class,
			PipeHeartBeatPacket.PONG_FACTORY,
			RelayPacketHandler::onPipeHeartBeat),

	TUBULE_CONNECT(RELAY_PACKET_TYPE_TUBULE_CONNECT,
			TubuleConnectPacket.class, TubuleConnectArguments.class,
			TubuleConnectPacket.FACTORY,
			RelayPacketHandler::onTubuleConnect),

	TUBULE_DISCONNECT(RELAY_PACKET_TYPE_TUBULE_DISCONNECT,
			TubuleDisconnectPacket.class, VoidPacketArguments.class,
			TubuleDisconnectPacket.FACTORY,
			RelayPacketHandler::onTubuleDisconnect),

	TUBULE_CONNECTED(RELAY_PACKET_TYPE_TUBULE_CONNECTED,
			TubuleConnectedPacket.class, VoidPacketArguments.class,
			TubuleConnectedPacket.FACTORY,
			RelayPacketHandler::onTubuleConnected),

	TUBULE_DISCONNECTED(RELAY_PACKET_TYPE_TUBULE_CONNECTED,
			TubuleDisconnectedPacket.class, VoidPacketArguments.class,
			TubuleDisconnectedPacket.FACTORY,
			RelayPacketHandler::onTubuleDisconnected),

	TUBULE_MESSAGE(RELAY_PACKET_TYPE_TUBULE_MESSAGE,
			TubuleMessagePacket.class, TubuleMessageArguments.class,
			TubuleMessagePacket.FACTORY,
			RelayPacketHandler::onTubuleRelay),
	//
	;

	private final byte id;

	private final Class<? extends RelayPacket<?>> packetClass;

	private final Class<? extends RelayPacketArguments> argumentsClass;

	private RelayPacketFactory<RelayPacket<RelayPacketArguments>, RelayPacketArguments> packetFactory;

	private final RelayPackerHandlerInvoker<RelayPacket<?>> invoker;

	<A extends RelayPacketArguments, P extends RelayPacket<A>> RelayPacketType(int id,
			Class<P> packetClass, Class<A> argumentsClass,
			RelayPacketFactory<P, A> packetFactory,
			RelayPackerHandlerInvoker<P> invoker) {
		this.id = (byte)id;
		this.packetClass = packetClass;
		this.argumentsClass = argumentsClass;
		this.packetFactory = as(packetFactory);
		this.invoker = as(invoker);
	}

	@Override
	public void invoke(RelayPacketHandler handler, NetRelayPipe<?> pipe, RelayPacket<?> packet) throws InvokeHandlerException {
		if (packet == null) {
			throw new NullPointerException(format("invoke {} handler error, datagram is null", this));
		}
		if (this.packetClass.isInstance(packet)) {
			this.invoker.invoke(handler, pipe, packet);
		} else {
			throw new InvokeHandlerException(
					format("invoke {} handler error, datagram is {} instead of {}", this, packet.getClass(), this.packetClass));
		}
	}

	@Override
	public Byte getId() {
		return this.id;
	}

	public Class<? extends RelayPacket<?>> getPacketClass() {
		return packetClass;
	}

	public Class<? extends RelayPacketArguments> getArgumentsClass() {
		return argumentsClass;
	}

	public byte getOption() {
		return this.id;
	}

	public RelayPacket<RelayPacketArguments> createPacket(long tunnelId, RelayPacketArguments arguments, long time) {
		return packetFactory.createPacket(tunnelId, arguments, time);
	}
}
