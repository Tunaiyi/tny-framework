package com.tny.game.net.transport.message.common;

import com.tny.game.net.transport.Certificate;
import com.tny.game.net.transport.message.*;

public class CommonMessage<UID> extends AbstractNetMessage<UID> {

    private static final long serialVersionUID = 1L;

    public CommonMessage(Certificate<UID> certificate, NetMessageHeader header) {
        super(certificate, header);
    }

}

