package com.tny.game.net.dispatcher.listener;

import com.tny.game.net.dispatcher.CommandResult;
import com.tny.game.net.dispatcher.MethodHolder;
import com.tny.game.net.dispatcher.Request;

public class DispatcherMessageEvent {

    private Request request;

    private MethodHolder methodHolder;

    private CommandResult result;

    public DispatcherMessageEvent(Request request, MethodHolder methodHolder, CommandResult result) {
        this.request = request;
        this.methodHolder = methodHolder;
        this.result = result;
    }

    public DispatcherMessageEvent(Request request, MethodHolder methodHolder) {
        this(request, methodHolder, null);
    }

    /**
     * @return the request
     */
    public Request getRequest() {
        return this.request;
    }

    /**
     * @return the methodHolder
     */
    public MethodHolder getMethodHolder() {
        return this.methodHolder;
    }

    public CommandResult getResult() {
        return this.result;
    }

    public void setResult(CommandResult result) {
        if (result == null)
            throw new NullPointerException("result can not be null!");
        this.result = result;
    }

}
