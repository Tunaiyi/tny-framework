package com.tny.game.net.message.defalut;

import com.tny.game.net.message.MessageBuilder;
import com.tny.game.net.message.MessageBuilderFactory;

public class DefaultMessageBuilderFactory<UID> implements MessageBuilderFactory<UID> {

    @Override
    public MessageBuilder<UID> newMessageBuilder() {
        return new DefaultMessageBuilder<>();
    }
}
