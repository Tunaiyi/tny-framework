package com.tny.game.net.endpoint.event;

import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.function.Predicate;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class EndpointResendEvent<UID> extends BaseEndpointEvent<UID> implements EndpointOutputEvent<UID> {

    private Predicate<Message> filter;

    public EndpointResendEvent(NetTunnel<UID> tunnel, Predicate<Message> filter) {
        super(tunnel);
        this.filter = filter;
    }

    public Predicate<Message> getFilter() {
        return this.filter;
    }

    @Override
    public EndpointEventType getEventType() {
        return EndpointEventType.RECEIVE;
    }

}
