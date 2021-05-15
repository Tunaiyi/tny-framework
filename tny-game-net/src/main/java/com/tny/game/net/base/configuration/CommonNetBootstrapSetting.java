package com.tny.game.net.base.configuration;

import com.tny.game.net.base.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

public abstract class CommonNetBootstrapSetting implements NetBootstrapSetting {

    private NetAppContext appContext;

    private String name;

    private String messageFactory = "default" + MessageFactory.class.getSimpleName();

    private String endpointEventHandler = "default" + EndpointEventsBoxHandler.class.getSimpleName();

    private String certificateFactory = "default" + CertificateFactory.class.getSimpleName();

    protected CommonNetBootstrapSetting() {
    }

    protected CommonNetBootstrapSetting(NetAppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public NetAppContext getAppContext() {
        return this.appContext;
    }

    @Override
    public String getMessageFactory() {
        return this.messageFactory;
    }

    @Override
    public String getCertificateFactory() {
        return this.certificateFactory;
    }

    @Override
    public String getEndpointEventHandler() {
        return this.endpointEventHandler;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public CommonNetBootstrapSetting setName(String name) {
        this.name = name;
        return this;
    }

    public CommonNetBootstrapSetting setAppContext(NetAppContext appContext) {
        this.appContext = appContext;
        return this;
    }

    public CommonNetBootstrapSetting setMessageFactory(String messageFactory) {
        this.messageFactory = messageFactory;
        return this;
    }

    public CommonNetBootstrapSetting setEndpointEventHandler(String endpointEventHandler) {
        this.endpointEventHandler = endpointEventHandler;
        return this;
    }

    public CommonNetBootstrapSetting setCertificateFactory(String certificateFactory) {
        this.certificateFactory = certificateFactory;
        return this;
    }

}
