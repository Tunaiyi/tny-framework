package com.tny.game.net.relay.packet;

import com.tny.game.common.enums.*;
import com.tny.game.net.relay.*;
import com.tny.game.net.relay.exception.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/1 3:04 上午
 */
public enum RelayPacketType implements EnumIdentifiable<Byte>, RelayPackerHandlerInvoker<RelayPacket> {

    MESSAGE(0, MessagePacket.class, RelayPacketHandler::onMessage),

    CONNECT(1, ConnectPacket.class, RelayPacketHandler::onConnect),

    DISCONNECT(3, DisconnectPacket.class, RelayPacketHandler::onDisconnect),

    CONNECTED(4, ConnectedPacket.class, RelayPacketHandler::onConnected),

    DISCONNECTED(5, DisconnectedPacket.class, RelayPacketHandler::onDisconnected),

    HEART_BEAT(6, HeartbeatPacket.class, RelayPacketHandler::onHeartBeat),

    //
    ;

    private final byte id;

    private final Class<? extends RelayPacket> datagramClass;

    private final RelayPackerHandlerInvoker<RelayPacket> invoker;

    <D extends RelayPacket> RelayPacketType(int id, Class<D> datagramClass, RelayPackerHandlerInvoker<D> invoker) {
        this.id = (byte)id;
        this.datagramClass = datagramClass;
        this.invoker = as(invoker);
    }

    @Override
    public void invoke(RelayPacketHandler handler, NetPipe<?> pipe, RelayPacket datagram) throws InvokeHandlerException {
        if (datagram == null) {
            throw new NullPointerException(format("invoke {} handler error, datagram is null", this));
        }
        if (this.datagramClass.isInstance(datagram)) {
            this.invoker.invoke(handler, pipe, datagram);
        } else {
            throw new InvokeHandlerException(
                    format("invoke {} handler error, datagram is {} instead of {}", this, datagram.getClass(), this.datagramClass));
        }
    }

    @Override
    public Byte getId() {
        return this.id;
    }

    public byte getIdValue() {
        return this.id;
    }

}
