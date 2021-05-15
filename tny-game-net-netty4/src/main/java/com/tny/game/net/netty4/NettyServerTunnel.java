package com.tny.game.net.netty4;

import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class NettyServerTunnel<UID> extends NettyTunnel<UID, NetEndpoint<UID>> {

    public NettyServerTunnel(Channel channel, NetBootstrapContext<UID> bootstrapContext) {
        super(channel, TunnelMode.SERVER, bootstrapContext);
    }

    @Override
    protected boolean replayEndpoint(NetEndpoint<UID> endpoint) {
        Certificate<UID> certificate = this.getCertificate();
        if (!certificate.isAuthenticated()) {
            EndpointEventsBox<UID> oldEventBox = this.endpoint.getEventsBox();
            this.endpoint = endpoint;
            this.endpoint.takeOver(oldEventBox);
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
        if (this.channel == null || !this.channel.isActive()) {
            LOGGER.warn("open failed. channel {} is not active", this.channel);
            return false;
        }
        return true;
    }

}
