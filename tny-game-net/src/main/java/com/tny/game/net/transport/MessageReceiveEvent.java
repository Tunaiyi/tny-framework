package com.tny.game.net.transport;

import com.tny.game.net.transport.message.*;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class MessageReceiveEvent<UID> extends BaseMessageEvent<UID> implements MessageInputEvent<UID> {

    private Message<UID> message;

    private RespondFuture<UID> messageFuture;

    public MessageReceiveEvent(NetTunnel<UID> tunnel, Message<UID> message, RespondFuture<UID> messageFuture) {
        super(tunnel);
        this.message = message;
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

}
