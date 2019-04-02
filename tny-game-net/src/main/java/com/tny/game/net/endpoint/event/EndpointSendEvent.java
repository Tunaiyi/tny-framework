package com.tny.game.net.endpoint.event;


import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class EndpointSendEvent<UID> extends BaseEndpointEvent<UID> implements EndPointOutputEvent<UID> {

    private MessageContext<UID> context;

    public EndpointSendEvent(NetTunnel<UID> tunnel, MessageContext<UID> context) {
        super(tunnel);
        this.context = context;
    }

    public MessageContext<UID> getContext() {
        return context;
    }

    @Override
    public EndpointEventType getEventType() {
        return EndpointEventType.SEND;
    }

    public void fail(Throwable cause) {
        context.fail(cause);
    }

}
