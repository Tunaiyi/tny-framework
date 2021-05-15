package com.tny.game.net.endpoint.event;

import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class EndpointReceiveEvent<UID> extends BaseEndpointEvent<UID> implements EndpointInputEvent<UID> {

    private final Message message;

    private final RespondFuture<UID> respondFuture;

    public EndpointReceiveEvent(NetTunnel<UID> tunnel, Message message, RespondFuture<UID> respondFuture) {
        super(tunnel);
        this.message = message;
        this.respondFuture = respondFuture;
    }

    public Message getMessage() {
        return this.message;
    }

    public RespondFuture<UID> getRespondFuture() {
        return this.respondFuture;
    }

    public boolean hasRespondFuture() {
        return this.respondFuture != null;
    }

    @Override
    public EndpointEventType getEventType() {
        return EndpointEventType.RECEIVE;
    }

}
