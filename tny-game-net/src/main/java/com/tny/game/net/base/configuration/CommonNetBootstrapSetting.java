package com.tny.game.net.base.configuration;

import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.executor.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

public abstract class CommonNetBootstrapSetting implements NetBootstrapSetting {

    private NetAppContext appContext;

    private String name;

    private String messageDispatcher = "default" + MessageDispatcher.class.getSimpleName();

    private String commandTaskProcessor = "default" + CommandTaskProcessor.class.getSimpleName();

    private String messageFactory = "default" + MessageFactory.class.getSimpleName();

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
    public String getMessageDispatcher() {
        return this.messageDispatcher;
    }

    @Override
    public String getCommandTaskProcessor() {
        return this.commandTaskProcessor;
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

    public CommonNetBootstrapSetting setCertificateFactory(String certificateFactory) {
        this.certificateFactory = certificateFactory;
        return this;
    }

    public CommonNetBootstrapSetting setMessageDispatcher(String messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
        return this;
    }

    public CommonNetBootstrapSetting setCommandTaskProcessor(String commandTaskProcessor) {
        this.commandTaskProcessor = commandTaskProcessor;
        return this;
    }

}
