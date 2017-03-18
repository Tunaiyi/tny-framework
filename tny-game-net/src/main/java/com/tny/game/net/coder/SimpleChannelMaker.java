package com.tny.game.net.coder;

import com.tny.game.net.message.MessageBuilderFactory;
import io.netty.channel.Channel;

public class SimpleChannelMaker<C extends Channel> extends ChannelMaker<Channel> {

    public SimpleChannelMaker() {
    }

    public SimpleChannelMaker(MessageBuilderFactory messageBuilderFactory, DataPacketEncoder encoder, DataPacketDecoder decoder) {
        super(messageBuilderFactory, encoder, decoder);
    }

}
