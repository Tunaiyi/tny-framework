package com.tny.game.net.session;

import com.tny.game.net.message.Message;
import com.tny.game.net.message.RespondMessageFuture;
import com.tny.game.net.tunnel.*;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class SessionReceiveEvent<UID> extends BaseSessionEvent<UID> implements SessionInputEvent<UID> {

    private SessionEventType eventType;

    private Message<UID> message;

    private RespondMessageFuture<UID> messageFuture;

    public SessionReceiveEvent(NetTunnel<UID> tunnel, Message<UID> message, SessionEventType eventType, RespondMessageFuture<UID> messageFuture) {
        super(tunnel);
        this.message = message;
        this.eventType = eventType;
        this.messageFuture = messageFuture;
    }

    public Message<UID> getMessage() {
        return message;
    }

    public void completeResponse() {
        if (messageFuture != null) {
            messageFuture.complete(this.message);
        }
    }

    public boolean hasMessageFuture() {
        return messageFuture != null;
    }

    @Override
    public SessionEventType getEventType() {
        return eventType;
    }

}
