package com.tny.game.net.message.protoex;

import com.tny.game.net.message.MessageBuilder;
import com.tny.game.net.message.MessageBuilderFactory;

public class ProtoExMessageBuilderFactory<UID> implements MessageBuilderFactory<UID> {

    @Override
    public MessageBuilder<UID> newMessageBuilder() {
        return new ProtoExMessageBuilder<>();
    }
}
