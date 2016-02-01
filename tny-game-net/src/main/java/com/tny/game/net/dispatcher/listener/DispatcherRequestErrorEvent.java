package com.tny.game.net.dispatcher.listener;

import com.tny.game.net.dispatcher.MethodHolder;
import com.tny.game.net.dispatcher.Request;

public class DispatcherRequestErrorEvent extends DispatcherRequestEvent {

    private final Throwable exception;

    public DispatcherRequestErrorEvent(Request request,
                                       MethodHolder methodHolder, Throwable exception) {
        super(request, methodHolder);
        this.exception = exception;
    }

    public Throwable getException() {
        return this.exception;
    }

}
