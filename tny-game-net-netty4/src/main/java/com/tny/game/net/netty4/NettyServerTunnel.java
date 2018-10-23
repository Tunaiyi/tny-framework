package com.tny.game.net.netty4;

import com.tny.game.net.exception.TunnelException;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;

/**
 * Created by Kun Yang on 2017/9/11.
 */
public class NettyServerTunnel<UID> extends NettyTunnel<UID> {

    public NettyServerTunnel(Channel channel, Certificate<UID> certificate) {
        super(channel, certificate, TunnelMode.SERVER);
    }

    @Override
    protected boolean onOpen() {
        if (this.channel == null || !this.channel.isActive()) {
            throw new TunnelException("channel {} is not active", this.channel);
        }
        return true;
    }

}
