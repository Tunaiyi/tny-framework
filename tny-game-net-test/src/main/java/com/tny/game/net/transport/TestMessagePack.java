package com.tny.game.net.transport;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-27 14:42
 */
public class TestMessagePack {

    private RequestContext<Long> context;
    private NetMessage message;

    public TestMessagePack(MessageContext<Long> context, NetMessage message) {
        this.context = (RequestContext<Long>)context;
        this.message = message;
    }

    public NetMessage getMessage() {
        return this.message;
    }

    public MessageContext<Long> getContext() {
        return this.context;
    }

    public RequestContext<Long> getRequestContext() {
        return this.context;
    }

    public MessageMode getMode() {
        if (this.context != null) {
            return this.context.getMode();
        }
        if (this.message != null) {
            return this.message.getMode();
        }
        return null;
    }

}
