package com.tny.game.net.kafka;

import com.tny.game.net.dispatcher.AbstractNetFuture;
import com.tny.game.net.dispatcher.Session;
import io.netty.channel.ChannelFuture;

/**
 * Created by Kun Yang on 16/9/20.
 */
public class KafkaNetFuture extends AbstractNetFuture<ChannelFuture> {

    protected KafkaNetFuture(Session session, ChannelFuture future) {
        super(session, future);
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    protected void addRealListener(Runnable firer) {

    }

}
