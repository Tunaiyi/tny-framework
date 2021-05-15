package com.tny.game.net.message;

import com.tny.game.common.context.*;

/**
 * Created by Kun Yang on 2017/3/30.
 */
public final class TickMessage extends AbstractNetMessage implements NetMessage, Message {

    private static final TickMessage PING = new TickMessage(TickMessageHead.ping());

    private static final TickMessage PONG = new TickMessage(TickMessageHead.pong());

    public static NetMessage ping() {
        return PING;
    }

    public static NetMessage pong() {
        return PONG;
    }

    private TickMessage(NetMessageHead head) {
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
