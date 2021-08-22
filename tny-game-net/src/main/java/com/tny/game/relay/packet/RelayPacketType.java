package com.tny.game.relay.packet;

import com.tny.game.common.enums.*;
import com.tny.game.relay.*;
import com.tny.game.relay.exception.*;
import com.tny.game.relay.packet.arguments.*;
import com.tny.game.relay.transport.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.relay.RelayCodecConstants.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/1 3:04 上午
 */
public enum RelayPacketType implements EnumIdentifiable<Byte>, RelayPackerHandlerInvoker<RelayPacket<?>> {

	LINK_OPEN(RELAY_PACKET_TYPE_LINK_OPEN,
			LinkOpenPacket.class, VoidPacketArguments.class,
			LinkOpenPacket.FACTORY,
			RelayPacketHandler::onPipeOpen),

	LINK_CLOSE(RELAY_PACKET_TYPE_LINK_CLOSE,
			LinkClosePacket.class, VoidPacketArguments.class,
			LinkClosePacket.FACTORY,
			RelayPacketHandler::onPipeClose),

	LINK_PING(RELAY_PACKET_TYPE_LINK_PING,
			LinkHeartBeatPacket.class, VoidPacketArguments.class,
			LinkHeartBeatPacket.PING_FACTORY,
			RelayPacketHandler::onPipeHeartBeat),

	LINK_PONG(RELAY_PACKET_TYPE_LINK_PONG,
			LinkHeartBeatPacket.class, VoidPacketArguments.class,
			LinkHeartBeatPacket.PONG_FACTORY,
			RelayPacketHandler::onPipeHeartBeat),

	TUNNEL_CONNECT(RELAY_PACKET_TYPE_TUNNEL_CONNECT,
			TunnelConnectPacket.class, TunnelConnectArguments.class,
			TunnelConnectPacket.FACTORY,
			RelayPacketHandler::onTunnelConnect),

	TUNNEL_DISCONNECT(RELAY_PACKET_TYPE_TUNNEL_DISCONNECT,
			TunnelDisconnectPacket.class, VoidPacketArguments.class,
			TunnelDisconnectPacket.FACTORY,
			RelayPacketHandler::onTunnelDisconnect),

	TUNNEL_CONNECTED(RELAY_PACKET_TYPE_TUNNEL_CONNECTED,
			TunnelConnectedPacket.class, TunnelConnectedArguments.class,
			TunnelConnectedPacket.FACTORY,
			RelayPacketHandler::onTunnelConnected),

	TUNNEL_DISCONNECTED(RELAY_PACKET_TYPE_TUNNEL_DISCONNECTED,
			TunnelDisconnectedPacket.class, VoidPacketArguments.class,
			TunnelDisconnectedPacket.FACTORY,
			RelayPacketHandler::onTunnelDisconnected),

	TUNNEL_RELAY(RELAY_PACKET_TYPE_TUNNEL_RELAY,
			TunnelRelayPacket.class, TunnelRelayArguments.class,
			TunnelRelayPacket.FACTORY,
			RelayPacketHandler::onTunnelRelay),
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
	public void invoke(RelayPacketHandler handler, NetRelayLink link, RelayPacket<?> packet) throws InvokeHandlerException {
		if (packet == null) {
			throw new NullPointerException(format("invoke {} handler error, datagram is null", this));
		}
		if (this.packetClass.isInstance(packet)) {
			this.invoker.invoke(handler, link, packet);
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
