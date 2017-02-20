package com.tny.game.net.kafka;

import com.tny.game.net.dispatcher.AbstractNetSendFuture;
import com.tny.game.net.dispatcher.Session;
import io.netty.channel.ChannelFuture;

/**
 * Created by Kun Yang on 16/9/20.
 */
public class KafkaNetSendFuture extends AbstractNetSendFuture<ChannelFuture> {

    protected KafkaNetSendFuture(Session session, ChannelFuture future) {
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
