package com.tny.game.net.command.listener;

import com.tny.game.net.message.Message;
import com.tny.game.net.common.dispatcher.MethodControllerHolder;
import com.tny.game.net.exception.DispatchException;

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
