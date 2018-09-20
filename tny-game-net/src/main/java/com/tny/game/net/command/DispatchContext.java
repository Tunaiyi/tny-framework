package com.tny.game.net.command;

import com.tny.game.net.command.dispatcher.MethodControllerHolder;
import com.tny.game.net.transport.*;
import com.tny.game.net.transport.message.Message;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/5/27.
 */
public abstract class DispatchContext extends InvokeContext {

    protected NetTunnel<Object> tunnel;

    protected Message<Object> message;

    protected MessageReceiveEvent<Object> event;

    public DispatchContext(MethodControllerHolder methodHolder) {
        super(methodHolder);
    }

    protected DispatchContext(MethodControllerHolder methodHolder, MessageReceiveEvent<?> event) {
        super(methodHolder);
        this.tunnel = as(event.getTunnel());
        this.message = as(event.getMessage());
        this.event = as(event);
    }

    public <T> Tunnel<T> getTunnel() {
        return as(tunnel);
    }

    public <T> Message<T> getMessage() {
        return as(message);
    }
}

