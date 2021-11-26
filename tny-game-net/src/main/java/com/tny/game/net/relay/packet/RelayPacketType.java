package com.tny.game.net.relay.packet;

import com.tny.game.common.enums.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.exception.*;
import com.tny.game.net.relay.link.*;
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
public enum RelayPacketType implements ByteEnumerable {

	LINK_OPENING(RELAY_PACKET_TYPE_LINK_OPENING,
			LinkOpenPacket.class, LinkOpenArguments.class,
			LinkOpenPacket.FACTORY,
			RelayPacketProcessor::onLinkOpen),

	LINK_OPENED(RELAY_PACKET_TYPE_LINK_OPENED,
			LinkOpenedPacket.class, LinkOpenedArguments.class,
			LinkOpenedPacket.FACTORY,
			RelayPacketProcessor::onLinkOpened),

	LINK_CLOSE(RELAY_PACKET_TYPE_LINK_CLOSE,
			LinkClosePacket.class, LinkVoidArguments.class,
			LinkClosePacket.FACTORY,
			RelayPacketProcessor::onLinkClose),

	LINK_PING(RELAY_PACKET_TYPE_LINK_PING,
			LinkHeartBeatPacket.class, LinkVoidArguments.class,
			LinkHeartBeatPacket.PING_FACTORY,
			RelayPacketProcessor::onLinkHeartBeat),

	LINK_PONG(RELAY_PACKET_TYPE_LINK_PONG,
			LinkHeartBeatPacket.class, LinkVoidArguments.class,
			LinkHeartBeatPacket.PONG_FACTORY,
			RelayPacketProcessor::onLinkHeartBeat),

	TUNNEL_CONNECT(RELAY_PACKET_TYPE_TUNNEL_CONNECT,
			TunnelConnectPacket.class, TunnelConnectArguments.class,
			TunnelConnectPacket.FACTORY,
			RelayPacketProcessor::onTunnelConnect),

	TUNNEL_CONNECTED(RELAY_PACKET_TYPE_TUNNEL_CONNECTED,
			TunnelConnectedPacket.class, TunnelConnectedArguments.class,
			TunnelConnectedPacket.FACTORY,
			RelayPacketProcessor::onTunnelConnected),

	TUNNEL_DISCONNECT(RELAY_PACKET_TYPE_TUNNEL_DISCONNECT,
			TunnelDisconnectPacket.class, TunnelVoidArguments.class,
			TunnelDisconnectPacket.FACTORY,
			RelayPacketProcessor::onTunnelDisconnect),

	TUNNEL_SWITCH_LINK(RELAY_PACKET_TYPE_TUNNEL_SWITCH_LINK,
			TunnelSwitchLinkPacket.class, TunnelVoidArguments.class,
			TunnelSwitchLinkPacket.FACTORY,
			RelayPacketProcessor::onTunnelSwitchLink),

	TUNNEL_RELAY(RELAY_PACKET_TYPE_TUNNEL_RELAY,
			TunnelRelayPacket.class, TunnelRelayArguments.class,
			TunnelRelayPacket.FACTORY,
			RelayPacketProcessor::onTunnelRelay),
	//
	;

	private final byte id;

	private final Class<? extends RelayPacket<?>> packetClass;

	private final Class<? extends RelayPacketArguments> argumentsClass;

	private final RelayPacketFactory<RelayPacket<RelayPacketArguments>, RelayPacketArguments> packetFactory;

	private final RelayPacketHandleByLinkInvoker<RelayPacket<?>> handleByLink;

	private final RelayPacketHandleByTransporterInvoker<RelayPacket<?>> handleByTransporter;

	<A extends RelayPacketArguments, P extends RelayPacket<A>> RelayPacketType(int id,
			Class<P> packetClass, Class<A> argumentsClass,
			RelayPacketFactory<P, A> packetFactory,
			RelayPacketHandleByLinkInvoker<P> packetHandlerInvoker) {
		this(id, packetClass, argumentsClass, packetFactory, packetHandlerInvoker, null);
	}

	<A extends RelayPacketArguments, P extends RelayPacket<A>> RelayPacketType(int id,
			Class<P> packetClass, Class<A> argumentsClass,
			RelayPacketFactory<P, A> packetFactory,
			RelayPacketHandleByTransporterInvoker<P> transporterHandlerInvoker) {
		this(id, packetClass, argumentsClass, packetFactory, null, transporterHandlerInvoker);
	}

	<A extends RelayPacketArguments, P extends RelayPacket<A>> RelayPacketType(int id,
			Class<P> packetClass, Class<A> argumentsClass,
			RelayPacketFactory<P, A> packetFactory,
			RelayPacketHandleByLinkInvoker<P> packetHandlerInvoker,
			RelayPacketHandleByTransporterInvoker<?> transporterHandlerInvoker) {
		this.id = (byte)id;
		this.packetClass = packetClass;
		this.argumentsClass = argumentsClass;
		this.packetFactory = as(packetFactory);
		this.handleByLink = as(packetHandlerInvoker);
		this.handleByTransporter = as(transporterHandlerInvoker);
	}

	public void handle(RelayPacketProcessor handler, NetRelayLink link, RelayPacket<?> packet) throws InvokeHandlerException {
		if (packet == null) {
			throw new NullPointerException(format("invoke {} handler error, datagram is null", this));
		}
		if (this.packetClass.isInstance(packet)) {
			this.handleByLink.invoke(handler, link, packet);
		} else {
			throw new InvokeHandlerException(
					format("invoke {} handler error, datagram is {} instead of {}", this, packet.getClass(), this.packetClass));
		}
	}

	public void handle(RelayPacketProcessor handler, NetRelayTransporter transporter, RelayPacket<?> packet) throws InvokeHandlerException {
		if (packet == null) {
			throw new NullPointerException(format("invoke {} handler error, datagram is null", this));
		}
		if (this.packetClass.isInstance(packet)) {
			this.handleByTransporter.invoke(handler, transporter, packet);
		} else {
			throw new InvokeHandlerException(
					format("invoke {} handler error, datagram is {} instead of {}", this, packet.getClass(), this.packetClass));
		}
	}

	public boolean isHandleByLink() {
		return handleByLink != null;
	}

	public boolean isHandleByTransporter() {
		return handleByTransporter != null;
	}

	@Override
	public byte id() {
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

	public RelayPacket<RelayPacketArguments> createPacket(int id, RelayPacketArguments arguments, long time) {
		return packetFactory.createPacket(id, arguments, time);
	}

}
