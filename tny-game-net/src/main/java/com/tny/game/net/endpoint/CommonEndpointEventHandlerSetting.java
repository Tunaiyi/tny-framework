package com.tny.game.net.endpoint;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.executor.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 */
public class CommonEndpointEventHandlerSetting implements EndpointEventHandlerSetting {

    private int threads = Runtime.getRuntime().availableProcessors();

    private String handlerClass = ForkJoinEndpointEventHandler.class.getName();

    private String messageDispatcher = "default" + MessageDispatcher.class.getSimpleName();

    private String commandExecutor = "default" + MessageCommandExecutor.class.getSimpleName();

    public CommonEndpointEventHandlerSetting() {
    }

    public CommonEndpointEventHandlerSetting(String messageDispatcher, String commandExecutor) {
        this.messageDispatcher = messageDispatcher;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public String getHandlerClass() {
        return handlerClass;
    }

    @Override
    public int getThreads() {
        return threads;
    }

    @Override
    public String getMessageDispatcher() {
        return messageDispatcher;
    }

    @Override
    public String getCommandExecutor() {
        return commandExecutor;
    }

    public CommonEndpointEventHandlerSetting setMessageDispatcher(String messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
        return this;
    }

    public CommonEndpointEventHandlerSetting setCommandExecutor(String commandExecutor) {
        this.commandExecutor = commandExecutor;
        return this;
    }

    public CommonEndpointEventHandlerSetting setThreads(int threads) {
        this.threads = threads;
        return this;
    }
}
