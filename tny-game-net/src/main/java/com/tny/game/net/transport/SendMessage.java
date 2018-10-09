package com.tny.game.net.transport;

import com.tny.game.net.transport.message.*;


/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-10-05 12:12
 */
public class SendMessage<UID> {

    private MessageSubject subject;

    private MessageContext<UID> context;

    public SendMessage(MessageSubject subject, MessageContext<UID> context) {
        this.subject = subject;
        this.context = context;
    }

    public MessageSubject getSubject() {
        return subject;
    }

    public MessageContext<UID> getContext() {
        return context;
    }
}
