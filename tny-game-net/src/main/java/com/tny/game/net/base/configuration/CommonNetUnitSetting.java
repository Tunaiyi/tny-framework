package com.tny.game.net.base.configuration;


import com.tny.game.net.base.*;
import com.tny.game.net.command.MessageHandler;
import com.tny.game.net.message.protoex.ProtoExMessageFactory;

import static com.tny.game.common.utils.ObjectAide.*;

public abstract class CommonNetUnitSetting implements NetUnitSetting {

    private AppContext appContext;

    private String name;

    private String messageFactory = ProtoExMessageFactory.class.getSimpleName();

    private String messageHandler;

    protected CommonNetUnitSetting() {
    }

    protected CommonNetUnitSetting(AppContext appContext) {
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
    public String getMessageHandler() {
        return messageHandler;
    }

    @Override
    public String getName() {
        return name;
    }

    public CommonNetUnitSetting setName(String name) {
        this.name = name;
        this.messageHandler = ifNullAndGet(this.messageHandler, () -> name + MessageHandler.class.getSimpleName());
        return this;
    }

    public CommonNetUnitSetting setAppContext(AppContext appContext) {
        this.appContext = appContext;
        return this;
    }

    protected CommonNetUnitSetting setMessageFactory(String messageFactory) {
        this.messageFactory = messageFactory;
        return this;
    }

    protected CommonNetUnitSetting setMessageHandler(String messageHandler) {
        this.messageHandler = messageHandler;
        return this;
    }
}
