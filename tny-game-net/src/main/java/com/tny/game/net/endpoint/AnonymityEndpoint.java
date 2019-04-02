package com.tny.game.net.endpoint;

import com.tny.game.net.transport.*;

/**
 * <p>
 */
public class AnonymityEndpoint<UID> extends AbstractEndpoint<UID> {

    private NetTunnel<UID> tunnel;

    public AnonymityEndpoint(NetTunnel<UID> tunnel, UID unloginUid,
                             EndpointEventHandler<UID, NetEndpoint<UID>> eventHandler) {
        super(unloginUid, eventHandler, 0);
        this.tunnel = tunnel;
    }

    @Override
    protected NetTunnel<UID> currentTunnel() {
        return tunnel;
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

}
