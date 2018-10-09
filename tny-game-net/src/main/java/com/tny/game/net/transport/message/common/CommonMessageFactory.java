package com.tny.game.net.transport.message.common;

import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.transport.Certificate;
import com.tny.game.net.transport.message.*;

@Unit("CommonMessageBuilderFactory")
public class CommonMessageFactory<UID> implements MessageFactory<UID> {

    public CommonMessageFactory() {
    }

    @Override
    public NetMessage<UID> create(long id, MessageSubject subject, Certificate<UID> certificate) {
        return new CommonMessage<>(certificate, new CommonMessageHeader(id, subject));
    }
}
