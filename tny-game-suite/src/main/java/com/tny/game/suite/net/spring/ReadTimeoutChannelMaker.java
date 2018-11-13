package com.tny.game.suite.net.spring;

import com.tny.game.net.netty4.ChannelMaker;
import com.tny.game.net.netty4.codec.*;
import io.netty.channel.*;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

public class ReadTimeoutChannelMaker<C extends Channel> extends ChannelMaker<C> {

    private long idleTimeout = 180000;

    public ReadTimeoutChannelMaker() {
    }

    public ReadTimeoutChannelMaker(DataPacketEncoder encoder, DataPacketDecoder decoder, long idleTimeout) {
        super(encoder, decoder);
        this.idleTimeout = idleTimeout;
    }

    @Override
    protected void prepareAddCoder(ChannelPipeline channelPipeline) {
        channelPipeline.addLast(new ReadTimeoutHandler(idleTimeout, TimeUnit.MILLISECONDS));
    }

    public long getIdleTimeout() {
        return idleTimeout;
    }

    public ReadTimeoutChannelMaker<C> setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
        return this;
    }

}
