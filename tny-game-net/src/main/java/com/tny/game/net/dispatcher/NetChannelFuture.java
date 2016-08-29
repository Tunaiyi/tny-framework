package com.tny.game.net.dispatcher;

import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Kun Yang on 16/8/9.
 */
public class NetChannelFuture extends AbstractNetFuture<ChannelFuture> {

    public static final Logger LOGGER = LoggerFactory.getLogger(NetChannelFuture.class);

    NetChannelFuture(Session session, ChannelFuture future) {
        super(session, future);
    }

    @Override
    protected void addRealListener(Runnable firer) {
        this.future.addListener(l -> firer.run());
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public boolean isSuccess() {
        return future.isSuccess();
    }

    @Override
    public Throwable cause() {
        return future.cause();
    }

}
