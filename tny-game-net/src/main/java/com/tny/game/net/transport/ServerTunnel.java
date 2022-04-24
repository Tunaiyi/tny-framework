package com.tny.game.net.transport;

import com.tny.game.net.base.*;
import com.tny.game.net.command.*;
import com.tny.game.net.command.task.*;
import com.tny.game.net.endpoint.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class ServerTunnel<UID, E extends NetSession<UID>, T extends MessageTransporter> extends BaseNetTunnel<UID, E, T> {

    public ServerTunnel(long id, T transporter, NetworkContext context) {
        super(id, transporter, TunnelMode.SERVER, context);
        this.bind(new AnonymityEndpoint<>(context.getCertificateFactory(), context, this));
    }

    @Override
    protected boolean replaceEndpoint(NetEndpoint<UID> newEndpoint) {
        Certificate<UID> certificate = this.getCertificate();
        if (!certificate.isAuthenticated()) {
            CommandTaskBox commandTaskBox = this.endpoint.getCommandTaskBox();
            this.endpoint = as(newEndpoint);
            this.endpoint.takeOver(commandTaskBox);
            return true;
        }
        return false;
    }

    @Override
    protected void onDisconnected() {
        this.close();
    }

    @Override
    protected boolean onOpen() {
        T transporter = this.transporter;
        if (transporter == null || !transporter.isActive()) {
            LOGGER.warn("open failed. channel {} is not active", transporter);
            return false;
        }
        return true;
    }

}
