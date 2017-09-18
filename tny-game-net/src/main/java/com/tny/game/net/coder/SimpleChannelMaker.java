package com.tny.game.net.coder;

import io.netty.channel.Channel;

public class SimpleChannelMaker<C extends Channel> extends ChannelMaker<Channel> {

    public SimpleChannelMaker() {
    }

    public SimpleChannelMaker(DataPacketEncoder encoder, DataPacketDecoder decoder) {
        super(encoder, decoder);
    }

}
