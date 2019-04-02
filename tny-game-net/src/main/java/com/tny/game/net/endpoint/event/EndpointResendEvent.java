package com.tny.game.net.endpoint.event;

import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;

import java.util.function.Predicate;

/**
 * Created by Kun Yang on 2017/2/17.
 */
public class EndpointResendEvent<UID> extends BaseEndpointEvent<UID> implements EndPointOutputEvent<UID> {

    private Predicate<Message<UID>> filter;

    public EndpointResendEvent(NetTunnel<UID> tunnel, Predicate<Message<UID>> filter) {
        super(tunnel);
        this.filter = filter;
    }

    public Predicate<Message<UID>> getFilter() {
        return filter;
    }


    @Override
    public EndpointEventType getEventType() {
        return EndpointEventType.RECEIVE;
    }

}
