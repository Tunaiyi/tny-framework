package com.tny.game.net.command;

import com.tny.game.common.utils.ObjectAide;
import com.tny.game.net.command.dispatcher.MethodControllerHolder;
import com.tny.game.net.message.Message;
import com.tny.game.net.tunnel.*;

/**
 * Created by Kun Yang on 2017/5/27.
 */
public abstract class DispatchContext extends InvokeContext {

    protected NetTunnel tunnel;

    protected Message message;

    public DispatchContext(MethodControllerHolder methodHolder) {
        super(methodHolder);
    }

    protected DispatchContext(MethodControllerHolder methodHolder, NetTunnel<?> tunnel, Message message) {
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

