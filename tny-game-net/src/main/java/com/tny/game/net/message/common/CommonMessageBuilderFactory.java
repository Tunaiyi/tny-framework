package com.tny.game.net.message.common;

import com.tny.game.suite.app.annotation.Unit;
import com.tny.game.net.message.MessageBuilder;
import com.tny.game.net.message.MessageBuilderFactory;

@Unit("CommonMessageBuilderFactory")
public class CommonMessageBuilderFactory<UID> implements MessageBuilderFactory<UID> {

    @Override
    public MessageBuilder<UID> newMessageBuilder() {
        return new CommonMessageBuilder<>();
    }
}
