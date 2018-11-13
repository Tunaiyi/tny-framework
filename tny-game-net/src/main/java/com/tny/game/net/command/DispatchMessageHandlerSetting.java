package com.tny.game.net.command;

import com.tny.game.net.command.dispatcher.DefaultMessageDispatcher;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-03 14:12
 */
public class DispatchMessageHandlerSetting {

    private String messageDispatcher = DefaultMessageDispatcher.class.getSimpleName();

    private String dispatchCommandExecutor;

    public DispatchMessageHandlerSetting() {
    }

    public DispatchMessageHandlerSetting(String messageDispatcher, String dispatchCommandExecutor) {
        this.messageDispatcher = messageDispatcher;
        this.dispatchCommandExecutor = dispatchCommandExecutor;
    }

    public String getMessageDispatcher() {
        return messageDispatcher;
    }

    public String getDispatchCommandExecutor() {
        return dispatchCommandExecutor;
    }

    public DispatchMessageHandlerSetting setMessageDispatcher(String messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
        return this;
    }

    public DispatchMessageHandlerSetting setDispatchCommandExecutor(String dispatchCommandExecutor) {
        this.dispatchCommandExecutor = dispatchCommandExecutor;
        return this;
    }
}
