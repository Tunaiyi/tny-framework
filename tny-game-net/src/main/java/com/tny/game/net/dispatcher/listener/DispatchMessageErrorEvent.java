package com.tny.game.net.dispatcher.listener;

import com.tny.game.net.base.Message;
import com.tny.game.net.dispatcher.MethodControllerHolder;

public class DispatchMessageErrorEvent extends ExecuteMessageEvent {

    private final Throwable exception;

    public DispatchMessageErrorEvent(Message message,
                                     MethodControllerHolder methodHolder, Throwable exception) {
        super(message, methodHolder);
        this.exception = exception;
    }

    public Throwable getException() {
        return this.exception;
    }

}
