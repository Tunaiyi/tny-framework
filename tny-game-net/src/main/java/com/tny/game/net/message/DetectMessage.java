package com.tny.game.net.message;

import com.tny.game.common.context.Attributes;

/**
 * Created by Kun Yang on 2017/3/30.
 */
public class DetectMessage<UID> extends AbstractNetMessage<UID> implements NetMessage<UID>, Message<UID> {

    public static <UID> NetMessage<UID> ping() {
        return new DetectMessage<>(DetectMessageHead.ping());
    }

    public static <UID> NetMessage<UID> pong() {
        return new DetectMessage<>(DetectMessageHead.pong());
    }

    private DetectMessage(NetMessageHead head) {
        super(head);
    }

    @Override
    public Attributes attributes() {
        throw new UnsupportedOperationException("DetectMessage unsupported attributes");
    }

    @Override
    public long getToMessage() {
        return 0;
    }

}
