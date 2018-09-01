package com.tny.game.net.message.common;

import com.tny.game.net.message.AbstractNetMessage;

public class CommonMessage<UID> extends AbstractNetMessage<UID> {

    private static final long serialVersionUID = 1L;

    public CommonMessage() {
        super();
    }

    @Override
    public CommonMessage<UID> setBody(Object body) {
        super.setBody(body);
        return this;
    }

    @Override
    protected AbstractNetMessage<UID> setSessionID(long sessionID) {
        super.setSessionID(sessionID);
        return this;
    }

}

