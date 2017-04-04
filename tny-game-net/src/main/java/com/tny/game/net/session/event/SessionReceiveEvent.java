package com.tny.game.net.session.event;

import com.tny.game.net.message.Message;
import com.tny.game.net.session.MessageFuture;
import com.tny.game.net.tunnel.Tunnel;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class SessionReceiveEvent<UID> extends BaseSessionEvent implements SessionInputEvent {

    private SessionEventType eventType;

    private Message<UID> message;

    private MessageFuture<?> messageFuture;

    public SessionReceiveEvent(Tunnel<UID> tunnel, Message<UID> message, SessionEventType eventType, MessageFuture<?> messageFuture) {
        super(tunnel);
        this.message = message;
        this.eventType = eventType;
        this.messageFuture = messageFuture;
    }

    public Message<UID> getMessage() {
        return message;
    }

    public MessageFuture<?> getMessageFuture() {
        return messageFuture;
    }

    public boolean hasMessageFuture() {
        return messageFuture != null;
    }

    @Override
    public SessionEventType getEventType() {
        return eventType;
    }

}
