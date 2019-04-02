package com.tny.game.net.message.common;

import com.tny.game.common.unit.annotation.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

@Unit
public class CommonMessageFactory<UID> implements MessageFactory<UID> {

    private final Certificate<UID> UNLOGIN_CERTIFICATE = Certificates.createUnautherized();

    public CommonMessageFactory() {
    }

    @Override
    public NetMessage<UID> create(long id, MessageSubject subject, Certificate<UID> certificate) {
        return new CommonMessage<>(certificate, new CommonMessageHead(id, subject), subject.getBody(Object.class), subject.getTail(Object.class));
    }

    @Override
    public NetMessage<UID> create(NetMessageHead head, Object body, Object tail) {
        return new CommonMessage<>(UNLOGIN_CERTIFICATE, head, body, tail);
    }

}
