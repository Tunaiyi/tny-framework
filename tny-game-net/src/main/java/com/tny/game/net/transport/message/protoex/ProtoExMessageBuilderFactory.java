package com.tny.game.net.transport.message.protoex;

import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.transport.message.MessageBuilder;
import com.tny.game.net.transport.message.MessageBuilderFactory;

@Unit("ProtoExMessageBuilderFactory")
public class ProtoExMessageBuilderFactory<UID> implements MessageBuilderFactory<UID> {

    @Override
    public MessageBuilder<UID> newBuilder() {
        return new ProtoExMessageBuilder<>();
    }
}
