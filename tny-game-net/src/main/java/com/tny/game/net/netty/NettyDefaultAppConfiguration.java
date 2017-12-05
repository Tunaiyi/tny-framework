package com.tny.game.net.netty;

import com.tny.game.net.netty.coder.ChannelMaker;
import com.tny.game.net.common.CommonAppConfiguration;
import io.netty.channel.Channel;

public class NettyDefaultAppConfiguration extends CommonAppConfiguration implements NettyAppConfiguration {

    private ChannelMaker<Channel> channelMaker;

    protected NettyDefaultAppConfiguration(String name) {
        super(name);
    }

    @Override
    public ChannelMaker<Channel> getChannelMaker() {
        return channelMaker;
    }

    public NettyDefaultAppConfiguration setChannelMaker(ChannelMaker<Channel> channelMaker) {
        this.channelMaker = channelMaker;
        return this;
    }

}
