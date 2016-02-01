package com.tny.game.net.dispatcher;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.util.concurrent.EventExecutor;

public class NetChannelPromise extends DefaultChannelPromise {

    private MessageFuture<?> future;

    private ClientSession session;

    public NetChannelPromise(ClientSession session, MessageFuture<?> future, Channel channel) {
        super(channel, channel.eventLoop());
        this.future = future;
        this.session = session;
    }

    public NetChannelPromise(ClientSession session, MessageFuture<?> future, Channel channel, EventExecutor executor) {
        super(channel, executor);
        this.future = future;
        this.session = session;
    }

    @Override
    public ChannelPromise setSuccess(Void result) {
        super.setSuccess(result);
        if (future != null && session != null) {
            session.putFuture(future);
        }
        return this;
    }

}
