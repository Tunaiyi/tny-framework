package com.tny.game.net.netty4.channel;

import io.netty.channel.ChannelPipeline;

public interface ChannelPipelineChain {

    default void beforeMake(ChannelPipeline channelPipeline) throws Exception {
    }

    default void afterMake(ChannelPipeline channelPipeline) throws Exception {
    }

}
