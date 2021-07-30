package com.tny.game.net.message.common;

import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.net.message.*;

@Unit
public class CommonMessageFactory implements MessageFactory {

    public CommonMessageFactory() {
    }

    @Override
    public NetMessage create(long id, MessageContent subject) {
        return new CommonMessage(new CommonMessageHead(id, subject), subject.getBody(Object.class));
    }

    @Override
    public NetMessage create(NetMessageHead head, Object body) {
        return new CommonMessage(head, body);
    }

}
