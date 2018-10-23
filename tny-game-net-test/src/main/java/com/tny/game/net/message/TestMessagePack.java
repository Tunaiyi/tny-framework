package com.tny.game.net.message;

import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-09-27 14:42
 */
public class TestMessagePack {

    private MessageSubject subject;
    private MessageContext<Long> context;
    private NetMessage<Long> message;

    public TestMessagePack(MessageSubject subject, NetMessage<Long> message) {
        this.subject = subject;
        this.message = message;
    }

    public MessageSubject getSubject() {
        return subject;
    }

    public MessageContext<Long> context() {
        if (context == null)
            context = MessageContexts.createContext();
        return context;
    }

    public NetMessage<Long> getMessage() {
        return message;
    }

    public MessageContext<Long> getContext() {
        return context;
    }

    public MessageMode getMode() {
        if (this.subject != null)
            return this.subject.getMode();
        if (this.message != null)
            return this.message.getMode();
        return null;
    }
}
