package com.tny.game.net.message.common;

import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

public class CommonMessageFactory<UID> implements MessageFactory<UID> {

    private final Certificate<UID> UNLOGIN_CERTIFICATE = Certificates.createUnautherized();

    public CommonMessageFactory() {
    }

    @Override
    public NetMessage<UID> create(long id, MessageContext<UID> context, Certificate<UID> certificate) {
        return new CommonMessage<>(certificate, new CommonMessageHeader(id, context), context.getBody());
    }

    @Override
    public NetMessage<UID> create(NetMessageHeader header, Object body) {
        return new CommonMessage<>(UNLOGIN_CERTIFICATE, header, body);
    }

}
