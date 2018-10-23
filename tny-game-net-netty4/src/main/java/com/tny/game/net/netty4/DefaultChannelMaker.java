package com.tny.game.net.netty4;

import com.tny.game.net.netty4.codec.*;
import io.netty.channel.Channel;

public class DefaultChannelMaker<C extends Channel> extends ChannelMaker<Channel> {

    public DefaultChannelMaker() {
    }

    public DefaultChannelMaker(DataPacketEncoder encoder, DataPacketDecoder decoder) {
        super(encoder, decoder);
    }

}
