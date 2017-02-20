package com.tny.game.net.coder;

import com.tny.game.net.checker.MessageChecker;
import com.tny.game.net.dispatcher.MessageBuilderFactory;
import io.netty.channel.Channel;

import java.util.List;

public class SimpleChannelMaker<C extends Channel> extends ChannelMaker<Channel> {

    public SimpleChannelMaker(List<MessageChecker> checkers) {
        super(checkers);
    }

    public SimpleChannelMaker(MessageBuilderFactory messageBuilderFactory, DataPacketEncoder encoder, DataPacketDecoder decoder, List<MessageChecker> checkers) {
        super(messageBuilderFactory, encoder, decoder, checkers);
    }

}
