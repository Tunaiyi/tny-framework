package com.tny.game.net.endpoint;

import com.tny.game.net.transport.*;

/**
 * <p>
 */
public class EndpointTestInstance<E extends NetEndpoint<Long>> {

    private E endpoint;

    private MockNetTunnel tunnel;

    private MockEndpointEventHandler<E> handler;

    public EndpointTestInstance(E endpoint, MockNetTunnel tunnel, MockEndpointEventHandler<E> handler) {
        this.endpoint = endpoint;
        this.tunnel = tunnel;
        this.handler = handler;
    }

    public E getEndpoint() {
        return endpoint;
    }

    public MockNetTunnel getTunnel() {
        return tunnel;
    }

    public MockEndpointEventHandler<E> getHandler() {
        return handler;
    }

}
