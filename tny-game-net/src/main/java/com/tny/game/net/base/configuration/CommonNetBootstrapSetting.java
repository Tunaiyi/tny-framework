package com.tny.game.net.base.configuration;

import com.tny.game.net.base.*;
import com.tny.game.net.codec.v1.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

public abstract class CommonNetBootstrapSetting implements NetBootstrapSetting {

    /**
     * 启动器名字
     */
    private String name;

    /**
     * 消息分发起名气
     */
    private String messageDispatcher = "default" + MessageDispatcher.class.getSimpleName();

    private String commandTaskProcessor = "default" + CommandTaskProcessor.class.getSimpleName();

    private String messageFactory = "default" + MessageFactory.class.getSimpleName();

    private String certificateFactory = "default" + CertificateFactory.class.getSimpleName();

    private DataPacketV1Config encoder = new DataPacketV1Config();

    private DataPacketV1Config decoder = new DataPacketV1Config();

    protected CommonNetBootstrapSetting() {
    }

    @Override
    public DataPacketV1Config getEncoder() {
        return this.encoder;
    }

    @Override
    public DataPacketV1Config getDecoder() {
        return this.decoder;
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

    public CommonNetBootstrapSetting setEncoder(DataPacketV1Config encoder) {
        this.encoder = encoder;
        return this;
    }

    public CommonNetBootstrapSetting setDecoder(DataPacketV1Config decoder) {
        this.decoder = decoder;
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
