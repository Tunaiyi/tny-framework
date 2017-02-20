package com.tny.game.net.dispatcher.listener;

import com.tny.game.net.dispatcher.MethodHolder;
import com.tny.game.net.dispatcher.Request;
import com.tny.game.net.dispatcher.exception.DispatchException;

public class DispatchExceptionEvent extends DispatcherMessageEvent {

    private final DispatchException exception;

    public DispatchExceptionEvent(Request request,
                                  MethodHolder methodHolder, DispatchException exception) {
        super(request, methodHolder);
        this.exception = exception;
    }

    public DispatchException getException() {
        return this.exception;
    }

}
