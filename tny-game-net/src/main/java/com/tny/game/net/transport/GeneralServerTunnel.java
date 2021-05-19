package com.tny.game.net.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.endpoint.task.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class GeneralServerTunnel<UID> extends GeneralTunnel<UID, NetEndpoint<UID>> {

    public GeneralServerTunnel(NetTransport<UID> transport, NetBootstrapContext<UID> bootstrapContext) {
        super(transport, TunnelMode.SERVER, bootstrapContext);
    }

    @Override
    protected boolean replayEndpoint(NetEndpoint<UID> endpoint) {
        Certificate<UID> certificate = this.getCertificate();
        if (!certificate.isAuthenticated()) {
            CommandTaskBox commandTaskBox = this.endpoint.getCommandTaskBox();
            this.endpoint = endpoint;
            this.endpoint.takeOver(commandTaskBox);
            return true;
        }
        return false;
    }

    @Override
    protected void onDisconnect() {
        this.close();
    }

    @Override
    protected boolean onOpen() {
        if (this.transport == null || !this.transport.isActive()) {
            LOGGER.warn("open failed. channel {} is not active", this.transport);
            return false;
        }
        return true;
    }

}
