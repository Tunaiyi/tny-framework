package com.tny.game.net.message.common;

import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.message.*;

@Unit("CommonMessageBuilderFactory")
public class CommonMessageBuilderFactory<UID> implements MessageBuilderFactory<UID> {

    public CommonMessageBuilderFactory() {
    }

    @Override
    public MessageBuilder<UID> newBuilder() {
        return new CommonMessageBuilder<>();
    }

}
