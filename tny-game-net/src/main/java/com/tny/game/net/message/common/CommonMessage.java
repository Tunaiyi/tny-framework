package com.tny.game.net.message.common;

import com.tny.game.net.message.*;
import com.tny.game.net.transport.Certificate;

public class CommonMessage<UID> extends AbstractNetMessage<UID> {

    private static final long serialVersionUID = 1L;

    public CommonMessage(Certificate<UID> certificate, NetMessageHeader header, Object body) {
        super(certificate, header, body);
    }

}

