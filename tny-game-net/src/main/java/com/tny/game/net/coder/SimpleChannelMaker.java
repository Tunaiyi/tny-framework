package com.tny.game.net.coder;

import com.tny.game.net.checker.RequestChecker;
import com.tny.game.net.dispatcher.MessageBuilderFactory;
import io.netty.channel.Channel;

public class SimpleChannelMaker<C extends Channel> extends ChannelMaker<Channel> {

    public SimpleChannelMaker(RequestChecker checker) {
        super(checker);
    }

    public SimpleChannelMaker(MessageBuilderFactory messageBuilderFactory, DataPacketEncoder encoder, DataPacketDecoder decoder, RequestChecker checker) {
        super(messageBuilderFactory, encoder, decoder, checker);
    }

}
