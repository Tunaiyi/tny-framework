package com.tny.game.net.message.common;

import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

@Unit("CommonMessageBuilderFactory")
public class CommonMessageFactory<UID> implements MessageFactory<UID> {

    private final Certificate<UID> UNLOGIN_CERTIFICATE = Certificates.createUnautherized();

    public CommonMessageFactory() {
    }

    @Override
    public NetMessage<UID> create(long id, MessageSubject subject, Object attachment, Certificate<UID> certificate) {
        return new CommonMessage<>(certificate, new CommonMessageHeader(id, subject, attachment), subject.getBody());
    }

    @Override
    public NetMessage<UID> create(NetMessageHeader header, Object body) {
        return new CommonMessage<>(UNLOGIN_CERTIFICATE, header, body);
    }

}
