package com.tny.game.net.message.protoex;

import com.tny.game.net.base.annotation.Unit;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.CommonMessage;
import com.tny.game.net.transport.*;

@Unit("ProtoExMessageBuilderFactory")
public class ProtoExMessageFactory<UID> implements MessageFactory<UID> {

    private final Certificate<UID> UNLOGIN_CERTIFICATE = Certificates.createUnautherized();

    @Override
    public NetMessage<UID> create(long id, MessageSubject subject, Object attachment, Certificate<UID> certificate) {
        return new CommonMessage<>(certificate, new ProtoExMessageHeader(id, subject, attachment), subject.getBody());
    }

    @Override
    public NetMessage<UID> create(NetMessageHeader header, Object body) {
        return new CommonMessage<>(UNLOGIN_CERTIFICATE, header, body);
    }

}
