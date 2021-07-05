package com.tny.game.net.agency.datagram;

import com.tny.game.common.enums.*;
import com.tny.game.net.agency.*;
import com.tny.game.net.agency.exception.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/3/1 3:04 上午
 */
public enum AgentDatagramHandlerType implements EnumIdentifiable<Byte>, AgentDatagramHandlerInvoker<TubuleDatagram> {

    MESSAGE(0, MessageDatagram.class, AgentDatagramHandler::onMessage),

    CONNECT(1, ConnectDatagram.class, AgentDatagramHandler::onConnect),

    DISCONNECT(3, DisconnectDatagram.class, AgentDatagramHandler::onDisconnect),

    CONNECTED(4, ConnectedDatagram.class, AgentDatagramHandler::onConnected),

    DISCONNECTED(5, DisconnectedDatagram.class, AgentDatagramHandler::onDisconnected),

    HEART_BEAT(6, HeartbeatDatagram.class, AgentDatagramHandler::onHeartBeat),

    //
    ;

    private final byte id;

    private final Class<? extends TubuleDatagram> datagramClass;

    private final AgentDatagramHandlerInvoker<TubuleDatagram> invoker;

    <D extends TubuleDatagram> AgentDatagramHandlerType(int id, Class<D> datagramClass, AgentDatagramHandlerInvoker<D> invoker) {
        this.id = (byte)id;
        this.datagramClass = datagramClass;
        this.invoker = as(invoker);
    }

    @Override
    public void invoke(AgentDatagramHandler handler, NetPipe<?> pipe, TubuleDatagram datagram) throws InvokeHandlerException {
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
