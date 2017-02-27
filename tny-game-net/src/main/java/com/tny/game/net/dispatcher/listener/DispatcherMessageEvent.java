package com.tny.game.net.dispatcher.listener;

import com.tny.game.net.base.Message;
import com.tny.game.net.dispatcher.CommandResult;
import com.tny.game.net.dispatcher.MethodControllerHolder;

public class DispatcherMessageEvent {

    private Message message;

    private MethodControllerHolder methodHolder;

    private CommandResult result;

    public DispatcherMessageEvent(Message message, MethodControllerHolder methodHolder, CommandResult result) {
        this.message = message;
        this.methodHolder = methodHolder;
        this.result = result;
    }

    public DispatcherMessageEvent(Message message, MethodControllerHolder methodHolder) {
        this(message, methodHolder, null);
    }

    /**
     * @return the request
     */
    public Message getMessage() {
        return this.message;
    }

    /**
     * @return the methodHolder
     */
    public MethodControllerHolder getMethodHolder() {
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
