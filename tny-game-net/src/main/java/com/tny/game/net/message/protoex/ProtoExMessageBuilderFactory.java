package com.tny.game.net.message.protoex;

import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.message.MessageBuilder;
import com.tny.game.net.message.MessageBuilderFactory;

@Unit("ProtoExMessageBuilderFactory")
public class ProtoExMessageBuilderFactory<UID> implements MessageBuilderFactory<UID> {

    @Override
    public MessageBuilder<UID> newMessageBuilder() {
        return new ProtoExMessageBuilder<>();
    }
}
