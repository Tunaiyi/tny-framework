package com.tny.game.net.endpoint;

import com.tny.game.net.transport.*;

/**
 * <p>
 */
public class EndpointTestInstance<E extends NetEndpoint<Long>> {

    private E endpoint;

    private MockNetTunnel tunnel;

    private MockEndpointEventsBoxHandler<E> handler;

    public EndpointTestInstance(E endpoint, MockNetTunnel tunnel, MockEndpointEventsBoxHandler<E> handler) {
        this.endpoint = endpoint;
        this.tunnel = tunnel;
        this.handler = handler;
    }

    public E getEndpoint() {
        return this.endpoint;
    }

    public MockNetTunnel getTunnel() {
        return this.tunnel;
    }

    public MockEndpointEventsBoxHandler<E> getHandler() {
        return this.handler;
    }

}
