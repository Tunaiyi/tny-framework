package com.tny.game.net.command.listener;

import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.net.command.DispatchCommand;
import com.tny.game.net.common.dispatcher.MethodControllerHolder;
import com.tny.game.net.message.Message;

public class DispatchCommandEvent<UID> {

    protected DispatchCommand command;

    protected Message message;

    protected Tunnel<UID> tunnel;

    protected MethodControllerHolder controller;

    public DispatchCommandEvent(DispatchCommand command, Tunnel<UID> tunnel, Message message, MethodControllerHolder controller) {
        this.command = command;
        this.tunnel = tunnel;
        this.message = message;
        this.controller = controller;
    }

    /**
     * @return the methodHolder
     */
    public MethodControllerHolder getController() {
        return this.controller;
    }

    public boolean isHasController() {
        return this.controller != null;
    }

    public Message getMessage() {
        return message;
    }

    public boolean isHasMessage() {
        return message != null;
    }

    /**
     * @return the request
     */
    public DispatchCommand getCommand() {
        return this.command;
    }

    public Tunnel<UID> getTunnel() {
        return tunnel;
    }

}