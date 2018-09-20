package com.tny.game.net.transport.message.common;

import com.tny.game.net.transport.message.AbstractNetMessage;

public class CommonMessage<UID> extends AbstractNetMessage<UID> {

    private static final long serialVersionUID = 1L;

    public CommonMessage() {
    }

    @Override
    public CommonMessage<UID> setBody(Object body) {
        super.setBody(body);
        return this;
    }

    @Override
    public void setId(long id) {
    }

}

