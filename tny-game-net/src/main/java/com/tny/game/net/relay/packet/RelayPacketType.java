/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.tny.game.net.relay.packet;

import com.tny.game.common.enums.*;
import com.tny.game.net.message.*;
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

    LINK_OPEN(RELAY_PACKET_TYPE_LINK_OPENING,
            LinkOpenPacket.class, LinkOpenArguments.class,
            LinkOpenPacket.FACTORY,
            RelayPacketProcessor::onLinkOpen, NetworkWay.SYSTEM),

    LINK_OPENED(RELAY_PACKET_TYPE_LINK_OPENED,
            LinkOpenedPacket.class, LinkOpenedArguments.class,
            LinkOpenedPacket.FACTORY,
            RelayPacketProcessor::onLinkOpened, NetworkWay.SYSTEM),

    LINK_CLOSE(RELAY_PACKET_TYPE_LINK_CLOSE,
            LinkClosePacket.class, LinkVoidArguments.class,
            LinkClosePacket.FACTORY,
            RelayPacketProcessor::onLinkClose, NetworkWay.SYSTEM),

    LINK_PING(RELAY_PACKET_TYPE_LINK_PING,
            LinkHeartBeatPacket.class, LinkVoidArguments.class,
            LinkHeartBeatPacket.PING_FACTORY,
            RelayPacketProcessor::onLinkHeartBeat, NetworkWay.HEARTBEAT),

    LINK_PONG(RELAY_PACKET_TYPE_LINK_PONG,
            LinkHeartBeatPacket.class, LinkVoidArguments.class,
            LinkHeartBeatPacket.PONG_FACTORY,
            RelayPacketProcessor::onLinkHeartBeat, NetworkWay.HEARTBEAT),

    TUNNEL_CONNECT(RELAY_PACKET_TYPE_TUNNEL_CONNECT,
            TunnelConnectPacket.class, TunnelConnectArguments.class,
            TunnelConnectPacket.FACTORY,
            RelayPacketProcessor::onTunnelConnect, NetworkWay.SYSTEM),

    TUNNEL_CONNECTED(RELAY_PACKET_TYPE_TUNNEL_CONNECTED,
            TunnelConnectedPacket.class, TunnelConnectedArguments.class,
            TunnelConnectedPacket.FACTORY,
            RelayPacketProcessor::onTunnelConnected, NetworkWay.SYSTEM),

    TUNNEL_DISCONNECT(RELAY_PACKET_TYPE_TUNNEL_DISCONNECT,
            TunnelDisconnectPacket.class, TunnelVoidArguments.class,
            TunnelDisconnectPacket.FACTORY,
            RelayPacketProcessor::onTunnelDisconnect, NetworkWay.SYSTEM),

    TUNNEL_SWITCH_LINK(RELAY_PACKET_TYPE_TUNNEL_SWITCH_LINK,
            TunnelSwitchLinkPacket.class, TunnelVoidArguments.class,
            TunnelSwitchLinkPacket.FACTORY,
            RelayPacketProcessor::onTunnelSwitchLink, NetworkWay.SYSTEM),

    TUNNEL_RELAY(RELAY_PACKET_TYPE_TUNNEL_RELAY,
            TunnelRelayPacket.class, TunnelRelayArguments.class,
            TunnelRelayPacket.FACTORY,
            RelayPacketProcessor::onTunnelRelay, NetworkWay.MESSAGE),
    //
    ;

    private final byte id;

    private NetworkWay way;

    private final Class<? extends RelayPacket<?>> packetClass;

    private final Class<? extends RelayPacketArguments> classOfArguments;

    private final RelayPacketFactory<RelayPacket<RelayPacketArguments>, RelayPacketArguments> packetFactory;

    private final RelayPacketHandleByLinkInvoker<RelayPacket<?>> handleByLink;

    private final RelayPacketHandleByTransporterInvoker<RelayPacket<?>> handleByTransporter;

    <A extends RelayPacketArguments, P extends RelayPacket<A>> RelayPacketType(int id,
            Class<P> packetClass, Class<A> classOfArguments, RelayPacketFactory<P, A> packetFactory,
            RelayPacketHandleByLinkInvoker<P> packetHandlerInvoker, NetworkWay way) {
        this(id, packetClass, classOfArguments, packetFactory, packetHandlerInvoker, null, way);
    }

    <A extends RelayPacketArguments, P extends RelayPacket<A>> RelayPacketType(int id,
            Class<P> packetClass, Class<A> classOfArguments, RelayPacketFactory<P, A> packetFactory,
            RelayPacketHandleByTransporterInvoker<P> transporterHandlerInvoker, NetworkWay way) {
        this(id, packetClass, classOfArguments, packetFactory, null, transporterHandlerInvoker, way);
    }

    <A extends RelayPacketArguments, P extends RelayPacket<A>> RelayPacketType(int id,
            Class<P> packetClass, Class<A> classOfArguments, RelayPacketFactory<P, A> packetFactory,
            RelayPacketHandleByLinkInvoker<P> packetHandlerInvoker, RelayPacketHandleByTransporterInvoker<?> transporterHandlerInvoker,
            NetworkWay way) {
        this.id = (byte)id;
        this.way = way;
        this.packetClass = packetClass;
        this.classOfArguments = classOfArguments;
        this.packetFactory = as(packetFactory);
        this.handleByLink = as(packetHandlerInvoker);
        this.handleByTransporter = as(transporterHandlerInvoker);
    }

    public void handle(RelayPacketProcessor handler, NetRelayLink link, RelayPacket<?> packet) throws RelayPacketHandleException {
        if (packet == null) {
            throw new NullPointerException(format("invoke {} handler error, datagram is null", this));
        }
        if (this.packetClass.isInstance(packet)) {
            this.handleByLink.invoke(handler, link, packet);
        } else {
            throw new RelayPacketHandleException("invoke {} handler error, datagram is {} instead of {}", this, packet.getClass(), this.packetClass);
        }
    }

    public void handle(RelayPacketProcessor handler, RelayTransporter transporter, RelayPacket<?> packet) throws RelayPacketHandleException {
        if (packet == null) {
            throw new NullPointerException(format("invoke {} handler error, datagram is null", this));
        }
        if (this.packetClass.isInstance(packet)) {
            this.handleByTransporter.invoke(handler, transporter, packet);
        } else {
            throw new RelayPacketHandleException("invoke {} handler error, datagram is {} instead of {}", this, packet.getClass(), this.packetClass);
        }
    }

    @Override
    public byte id() {
        return this.id;
    }

    public boolean isHandleByLink() {
        return handleByLink != null;
    }

    public boolean isHandleByTransporter() {
        return handleByTransporter != null;
    }

    public NetworkWay getWay() {
        return way;
    }

    public Class<? extends RelayPacket<?>> getPacketClass() {
        return packetClass;
    }

    public Class<? extends RelayPacketArguments> getClassOfArguments() {
        return classOfArguments;
    }

    public byte getOption() {
        return this.id;
    }

    public RelayPacket<RelayPacketArguments> createPacket(int id, RelayPacketArguments arguments, long time) {
        return packetFactory.createPacket(id, arguments, time);
    }

}
