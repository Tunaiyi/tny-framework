package com.tny.game.net.base;

import com.tny.game.net.command.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.transport.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/6 12:09 下午
 */
public class NetBootstrapContext implements NetworkContext {

    private final NetBootstrapSetting setting;

    private final MessageFactory messageFactory;

    private final CertificateFactory<?> certificateFactory;

    private final MessageDispatcher messageDispatcher;

    private final CommandTaskBoxProcessor commandTaskProcessor;

    private final RpcForwarder rpcForwarder;

    public NetBootstrapContext() {
        this.messageFactory = new CommonMessageFactory();
        this.certificateFactory = new DefaultCertificateFactory<>();
        this.setting = null;
        this.messageDispatcher = null;
        this.commandTaskProcessor = null;
        this.rpcForwarder = null;
    }

    public NetBootstrapContext(
            NetBootstrapSetting setting,
            MessageDispatcher messageDispatcher,
            CommandTaskBoxProcessor commandTaskProcessor,
            MessageFactory messageFactory,
            CertificateFactory<?> certificateFactory,
            RpcForwarder rpcForwarder) {
        this.setting = setting;
        this.messageDispatcher = messageDispatcher;
        this.commandTaskProcessor = commandTaskProcessor;
        this.messageFactory = messageFactory;
        this.certificateFactory = certificateFactory;
        this.rpcForwarder = rpcForwarder;
    }

    @Override
    public NetBootstrapSetting getSetting() {
        return setting;
    }

    @Override
    public MessageFactory getMessageFactory() {
        return this.messageFactory;
    }

    @Override
    public <I> CertificateFactory<I> getCertificateFactory() {
        return as(this.certificateFactory);
    }

    @Override
    public MessageDispatcher getMessageDispatcher() {
        return this.messageDispatcher;
    }

    @Override
    public CommandTaskBoxProcessor getCommandTaskProcessor() {
        return this.commandTaskProcessor;
    }

    @Override
    public RpcForwarder getRpcForwarder() {
        return rpcForwarder;
    }

}
