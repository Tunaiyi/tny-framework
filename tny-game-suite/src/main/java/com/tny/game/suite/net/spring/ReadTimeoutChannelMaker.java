package com.tny.game.suite.net.spring;

import com.tny.game.net.netty.coder.ChannelMaker;
import io.netty.channel.*;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

public class ReadTimeoutChannelMaker<C extends Channel> extends ChannelMaker<C> {

    private long timeout;

    public ReadTimeoutChannelMaker(long timeout) {
        this.timeout = timeout;
    }

    @Override
    protected void prepareAddCoder(ChannelPipeline channelPipeline) {
        channelPipeline.addLast(new ReadTimeoutHandler(timeout, TimeUnit.MILLISECONDS));
    }

}
