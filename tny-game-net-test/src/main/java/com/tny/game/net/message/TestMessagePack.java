package com.tny.game.net.message;

import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-27 14:42
 */
public class TestMessagePack {

    private RequestContext<Long> context;
    private NetMessage<Long> message;

    public TestMessagePack(MessageContext<Long> context, NetMessage<Long> message) {
        this.context = (RequestContext<Long>) context;
        this.message = message;
    }

    public NetMessage<Long> getMessage() {
        return message;
    }

    public MessageContext<Long> getContext() {
        return context;
    }

    public RequestContext<Long> getRequestContext() {
        return context;
    }

    public MessageMode getMode() {
        if (this.context != null)
            return this.context.getMode();
        if (this.message != null)
            return this.message.getMode();
        return null;
    }
}
