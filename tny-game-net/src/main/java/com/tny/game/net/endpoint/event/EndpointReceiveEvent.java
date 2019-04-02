package com.tny.game.net.endpoint.event;

import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class EndpointReceiveEvent<UID> extends BaseEndpointEvent<UID> implements EndPointInputEvent<UID> {

    private Message<UID> message;

    private RespondFuture<UID> respondFuture;

    public EndpointReceiveEvent(NetTunnel<UID> tunnel, Message<UID> message, RespondFuture<UID> respondFuture) {
        super(tunnel);
        this.message = message;
        this.respondFuture = respondFuture;
    }

    public Message<UID> getMessage() {
        return message;
    }

    public RespondFuture<UID> getRespondFuture() {
        return respondFuture;
    }

    public boolean hasRespondFuture() {
        return respondFuture != null;
    }

    @Override
    public EndpointEventType getEventType() {
        return EndpointEventType.RECEIVE;
    }

}
