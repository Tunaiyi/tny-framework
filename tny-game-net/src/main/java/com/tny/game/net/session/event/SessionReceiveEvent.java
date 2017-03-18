package com.tny.game.net.session.event;

import com.tny.game.net.message.Message;
import com.tny.game.net.session.MessageFuture;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class SessionReceiveEvent implements SessionInputEvent {

    private SessionEventType eventType;

    private Message message;

    private MessageFuture<?> messageFuture;

    public SessionReceiveEvent(Message message, SessionEventType eventType, MessageFuture<?> messageFuture) {
        this.message = message;
        this.eventType = eventType;
        this.messageFuture = messageFuture;
    }

    public Message getMessage() {
        return message;
    }

    public MessageFuture<?> getMessageFuture() {
        return messageFuture;
    }

    @Override
    public SessionEventType getEventType() {
        return eventType;
    }

}
