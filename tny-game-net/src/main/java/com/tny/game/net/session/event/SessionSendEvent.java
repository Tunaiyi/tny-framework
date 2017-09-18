package com.tny.game.net.session.event;

import com.tny.game.net.message.MessageContent;
import com.tny.game.net.message.MessageWriteFuture;
import com.tny.game.net.tunnel.Tunnel;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class SessionSendEvent<UID> extends BaseSessionEvent<UID> implements SessionOutputEvent<UID>, MessageWriteFuture<UID> {

    private MessageContent<UID> content;

    public SessionSendEvent(Tunnel<UID> tunnel, MessageContent<UID> content) {
        super(tunnel);
        this.content = content;
    }

    @Override
    public MessageContent<UID> getContent() {
        return content;
    }

    @Override
    public SessionEventType getEventType() {
        return SessionEventType.MESSAGE;
    }

}
