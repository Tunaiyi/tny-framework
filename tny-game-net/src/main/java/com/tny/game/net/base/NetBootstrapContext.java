package com.tny.game.net.base;

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
public class NetBootstrapContext<UID> {

    private final MessageFactory<UID> messageFactory;

    private final CertificateFactory<UID> certificateFactory;

    private final EndpointEventsBoxHandler<UID, NetEndpoint<UID>> endpointEndpointEventHandler;

    public NetBootstrapContext(EndpointEventsBoxHandler<UID, NetEndpoint<UID>> endpointEndpointEventHandler) {
        this.messageFactory = new CommonMessageFactory<>();
        this.certificateFactory = new DefaultCertificateFactory<>();
        this.endpointEndpointEventHandler = endpointEndpointEventHandler;
    }

    public NetBootstrapContext(
            EndpointEventsBoxHandler<UID, NetEndpoint<UID>> endpointEndpointEventHandler,
            MessageFactory<UID> messageFactory,
            CertificateFactory<UID> certificateFactory) {
        this.messageFactory = messageFactory;
        this.certificateFactory = certificateFactory;
        this.endpointEndpointEventHandler = endpointEndpointEventHandler;
    }

    public EndpointEventsBoxHandler<UID, NetEndpoint<UID>> getEndpointEndpointEventHandler() {
        return this.endpointEndpointEventHandler;
    }

    public MessageFactory<UID> getMessageFactory() {
        return this.messageFactory;
    }

    public CertificateFactory<UID> getCertificateFactory() {
        return this.certificateFactory;
    }

}
