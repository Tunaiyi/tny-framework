package com.tny.game.net.endpoint;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.executor.*;
import com.tny.game.net.transport.*;

import javax.annotation.Resource;

/**
 * <p>
 */
public class CommonEndpointEventHandlerSetting implements EndpointEventHandlerSetting {

    private int threads = Runtime.getRuntime().availableProcessors();

    private String eventHandlerClass = ForkJoinEndpointEventsBoxHandler.class.getName();

    private String endpointKeeperManager = CommonEndpointKeeperManager.class.getName();

    private String messageDispatcher = "default" + MessageDispatcher.class.getSimpleName();

    private String commandExecutor = "default" + MessageCommandExecutor.class.getSimpleName();

    public CommonEndpointEventHandlerSetting() {
    }

    @Resource
    private CertificateFactory<?> certificateFactory;

    public CommonEndpointEventHandlerSetting(String messageDispatcher, String commandExecutor) {
        this.messageDispatcher = messageDispatcher;
        this.commandExecutor = commandExecutor;
    }

    @Override
    public String getEventHandlerClass() {
        return this.eventHandlerClass;
    }

    @Override
    public int getThreads() {
        return this.threads;
    }

    @Override
    public String getMessageDispatcher() {
        return this.messageDispatcher;
    }

    @Override
    public String getCommandExecutor() {
        return this.commandExecutor;
    }

    @Override
    public String getEndpointKeeperManager() {
        return this.endpointKeeperManager;
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

    public CommonEndpointEventHandlerSetting setEventHandlerClass(String eventHandlerClass) {
        this.eventHandlerClass = eventHandlerClass;
        return this;
    }

    public CommonEndpointEventHandlerSetting setEndpointKeeperManager(String endpointKeeperManager) {
        this.endpointKeeperManager = endpointKeeperManager;
        return this;
    }

}
