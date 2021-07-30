package com.tny.game.net.message.common;

import com.tny.game.net.message.*;

public class CommonMessage extends AbstractNetMessage {

    private static final long serialVersionUID = 1L;

    public CommonMessage(NetMessageHead head, Object body) {
        super(head, body);
    }

}

