package com.tny.game.net.transport.message;

import com.tny.game.common.context.Attributes;

/**
 * Created by Kun Yang on 2017/3/30.
 */
public class DetectMessage<UID> extends AbstractNetMessage<UID> implements NetMessage<UID>, Message<UID> {

    public static <UID> NetMessage<UID> ping() {
        return new DetectMessage<>(DetectMessageHeader.ping());
    }

    public static <UID> NetMessage<UID> pong() {
        return new DetectMessage<>(DetectMessageHeader.pong());
    }

    private DetectMessage(NetMessageHeader header) {
        super(header);
    }

    @Override
    public Attributes attributes() {
        throw new UnsupportedOperationException("DetectMessage unsupported attributes");
    }

    @Override
    protected Object getBody() {
        return null;
    }

}
