package com.tny.game.net.base.configuration;

import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.apache.commons.lang3.StringUtils;

import static com.tny.game.common.utils.StringAide.*;
import static com.tny.game.net.base.configuration.NetUnitNames.*;

public abstract class CommonNetBootstrapSetting implements NetBootstrapSetting {

    /**
     * 服务名
     */
    private String name;

    /**
     * 是否是转发
     */
    private boolean forwardable = false;

    /**
     * 服务发现: 服务名
     */
    private String serveName;

    /**
     * 消息分发起名气
     */
    private String messageDispatcher = defaultName(MessageDispatcher.class);

    private String commandTaskProcessor = defaultName(CommandTaskBoxProcessor.class);

    private String messageFactory = defaultName(MessageFactory.class);

    private String certificateFactory = defaultName(CertificateFactory.class);

    private String tunnelIdGenerator = defaultName(NetIdGenerator.class);

    private String rpcForwarder = defaultName(RpcForwarder.class);

    protected CommonNetBootstrapSetting() {
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
    public String getTunnelIdGenerator() {
        return tunnelIdGenerator;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean isForwardable() {
        return forwardable;
    }

    @Override
    public String getServeName() {
        return serveName;
    }

    @Override
    public String getRpcForwarder() {
        return rpcForwarder;
    }

    @Override
    public String serviceName() {
        return ifBlank(this.name, serveName);
    }

    public CommonNetBootstrapSetting setName(String name) {
        if (StringUtils.isBlank(this.name)) {
            this.name = name;
        }
        return this;
    }

    public CommonNetBootstrapSetting setServeName(String serveName) {
        this.serveName = serveName;
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

    public CommonNetBootstrapSetting setTunnelIdGenerator(String tunnelIdGenerator) {
        this.tunnelIdGenerator = tunnelIdGenerator;
        return this;
    }

    public CommonNetBootstrapSetting setCommandTaskProcessor(String commandTaskProcessor) {
        this.commandTaskProcessor = commandTaskProcessor;
        return this;
    }

    public CommonNetBootstrapSetting setRpcForwarder(String rpcForwarder) {
        this.rpcForwarder = rpcForwarder;
        return this;
    }

    public CommonNetBootstrapSetting setForwardable(boolean forwardable) {
        this.forwardable = forwardable;
        return this;
    }

}
