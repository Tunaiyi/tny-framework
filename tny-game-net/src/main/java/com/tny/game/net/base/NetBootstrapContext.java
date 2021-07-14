package com.tny.game.net.base;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/6 12:09 下午
 */
public class NetBootstrapContext<UID> implements EndpointContext<UID> {

    private final MessageFactory messageFactory;

    private final CertificateFactory<UID> certificateFactory;

    private final MessageDispatcher messageDispatcher;

    private final CommandTaskProcessor commandTaskProcessor;

    public NetBootstrapContext() {
        this.messageFactory = new CommonMessageFactory();
        this.certificateFactory = new DefaultCertificateFactory<>();
        this.messageDispatcher = null;
        this.commandTaskProcessor = null;
    }

    public NetBootstrapContext(
            MessageDispatcher messageDispatcher,
            CommandTaskProcessor commandTaskProcessor,
            MessageFactory messageFactory,
            CertificateFactory<UID> certificateFactory) {
        this.messageDispatcher = messageDispatcher;
        this.commandTaskProcessor = commandTaskProcessor;
        this.messageFactory = messageFactory;
        this.certificateFactory = certificateFactory;
    }

    public MessageFactory getMessageFactory() {
        return this.messageFactory;
    }

    @Override
    public CertificateFactory<UID> getCertificateFactory() {
        return this.certificateFactory;
    }

    @Override
    public MessageDispatcher getMessageDispatcher() {
        return this.messageDispatcher;
    }

    @Override
    public CommandTaskProcessor getCommandTaskProcessor() {
        return this.commandTaskProcessor;
    }

}
