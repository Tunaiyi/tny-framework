package com.tny.game.net.base.configuration;


import com.tny.game.net.base.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

public abstract class CommonNetBootstrapSetting implements NetBootstrapSetting {

    private AppContext appContext;

    private String name;

    private String messageFactory = "default" + MessageFactory.class.getSimpleName();

    private String eventHandler = "default" + EndpointEventHandler.class.getSimpleName();

    protected CommonNetBootstrapSetting() {
    }

    protected CommonNetBootstrapSetting(AppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public AppContext getAppContext() {
        return appContext;
    }

    @Override
    public String getMessageFactory() {
        return messageFactory;
    }

    @Override
    public String getEventHandler() {
        return eventHandler;
    }

    @Override
    public String getName() {
        return name;
    }

    public CommonNetBootstrapSetting setName(String name) {
        this.name = name;
        return this;
    }

    public CommonNetBootstrapSetting setAppContext(AppContext appContext) {
        this.appContext = appContext;
        return this;
    }

    public CommonNetBootstrapSetting setMessageFactory(String messageFactory) {
        this.messageFactory = messageFactory;
        return this;
    }

    public CommonNetBootstrapSetting setEventHandler(String eventHandler) {
        this.eventHandler = eventHandler;
        return this;
    }
}
