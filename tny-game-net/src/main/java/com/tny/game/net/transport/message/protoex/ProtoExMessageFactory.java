package com.tny.game.net.transport.message.protoex;

import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.transport.Certificate;
import com.tny.game.net.transport.message.*;
import com.tny.game.net.transport.message.common.*;

@Unit("ProtoExMessageBuilderFactory")
public class ProtoExMessageFactory<UID> implements MessageFactory<UID> {

    @Override
    public NetMessage<UID> create(long id, MessageSubject subject, Certificate<UID> certificate) {
        return new CommonMessage<>(certificate, new ProtoExMessageHeader(id, subject));
    }

}
