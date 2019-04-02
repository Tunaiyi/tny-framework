package com.tny.game.net.netty4;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class NettyServerTunnel<UID> extends NettyTunnel<UID, NetEndpoint<UID>> {

    public NettyServerTunnel(Channel channel, UID unloginUid,
                             EndpointEventHandler<UID, NetEndpoint<UID>> eventHandler,
                             MessageFactory<UID> messageBuilderFactory) {
        super(channel, TunnelMode.SERVER, null, messageBuilderFactory);
        this.endpoint = new AnonymityEndpoint<>(this, unloginUid, eventHandler);
    }

    @Override
    public boolean bind(NetEndpoint<UID> endpoint) {
        if (endpoint == null || !endpoint.isLogin())
            return false;
        if (this.endpoint == endpoint)
            return true;
        synchronized (this) {
            if (this.endpoint == endpoint)
                return true;
            Certificate<UID> certificate = this.getCertificate();
            if (!certificate.isAutherized()) {
                EndpointEventsBox<UID> oldEventBox = this.endpoint.getEventsBox();
                this.endpoint = endpoint;
                this.endpoint.takeOver(oldEventBox);
                return true;
            }
        }
        return false;
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
