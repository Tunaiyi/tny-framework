package com.tny.game.net.command;

import com.tny.game.common.utils.ObjectAide;
import com.tny.game.net.common.dispatcher.MethodControllerHolder;
import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.Tunnel;

/**
 * Created by Kun Yang on 2017/5/27.
 */
public abstract class DispatchContext extends InvokeContext {

    protected Tunnel tunnel;

    protected Message message;

    public DispatchContext(MethodControllerHolder methodHolder) {
        super(methodHolder);
    }

    protected DispatchContext(MethodControllerHolder methodHolder, Tunnel tunnel, Message message) {
        super(methodHolder);
        this.tunnel = tunnel;
        this.message = message;
    }

    public <T> Tunnel<T> getTunnel() {
        return ObjectAide.as(tunnel);
    }

    public <T> Message<T> getMessage() {
        return ObjectAide.as(message);
    }
}

