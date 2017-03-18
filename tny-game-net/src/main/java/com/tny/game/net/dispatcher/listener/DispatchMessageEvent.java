package com.tny.game.net.dispatcher.listener;

import com.tny.game.net.message.Message;
import com.tny.game.net.dispatcher.MethodControllerHolder;

public class DispatchMessageEvent {

    protected Message message;

    protected MethodControllerHolder controller;

    public DispatchMessageEvent(Message message, MethodControllerHolder controller) {
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
        return this.controller == null;
    }

    /**
     * @return the request
     */
    public Message getMessage() {
        return this.message;
    }

}