package com.tny.game.net.command.listener;

import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.net.command.DispatchCommand;
import com.tny.game.net.common.dispatcher.MethodControllerHolder;
import com.tny.game.net.message.Message;

public class DispatchCommandExceptionEvent<UID> extends DispatchCommandExecuteEvent<UID> {

    private final Throwable exception;

    public DispatchCommandExceptionEvent(DispatchCommand command, Tunnel<UID> tunnel, Message<UID> message,
                                         MethodControllerHolder methodHolder, Throwable exception) {
        super(command, tunnel, message, methodHolder);
        this.exception = exception;
    }

    public Throwable getException() {
        return this.exception;
    }

}
