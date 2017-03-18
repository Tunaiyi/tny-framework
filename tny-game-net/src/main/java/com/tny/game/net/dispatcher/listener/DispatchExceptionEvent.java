package com.tny.game.net.dispatcher.listener;

import com.tny.game.net.message.Message;
import com.tny.game.net.dispatcher.MethodControllerHolder;
import com.tny.game.net.dispatcher.exception.DispatchException;

public class DispatchExceptionEvent extends ExecuteMessageEvent {

    private final DispatchException exception;

    public DispatchExceptionEvent(Message message,
                                  MethodControllerHolder methodHolder, DispatchException exception) {
        super(message, methodHolder);
        this.exception = exception;
    }

    public DispatchException getException() {
        return this.exception;
    }

}
