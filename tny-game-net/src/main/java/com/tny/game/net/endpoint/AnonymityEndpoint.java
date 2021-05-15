package com.tny.game.net.endpoint;

import com.tny.game.net.transport.*;

/**
 * <p>
 */
public class AnonymityEndpoint<UID> extends AbstractEndpoint<UID> {

    private NetTunnel<UID> tunnel;

    public AnonymityEndpoint(Certificate<UID> certificate, EndpointEventsBoxHandler<UID, ? extends NetEndpoint<UID>> eventHandler) {
        super(certificate, eventHandler, 0);
    }

    @Override
    protected NetTunnel<UID> currentTunnel() {
        return this.tunnel;
    }

    @Override
    public void onUnactivated(NetTunnel<UID> tunnel) {
        this.close();
    }

    @Override
    public void heartbeat() {
        this.tunnel.ping();
    }

    @Override
    public Certificate<UID> getCertificate() {
        return this.certificate;
    }

    public AnonymityEndpoint<UID> setTunnel(NetTunnel<UID> tunnel) {
        this.tunnel = tunnel;
        return this;
    }

}
