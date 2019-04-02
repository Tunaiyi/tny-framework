package com.tny.game.net.transport;

/**
 * <p>
 */
public class TunnelTestInstance<T, E extends MockNetEndpoint> {

    private T tunnel;

    private E endpoint;

    public TunnelTestInstance(T tunnel, E endpoint) {
        this.tunnel = tunnel;
        this.endpoint = endpoint;
    }

    public T getTunnel() {
        return tunnel;
    }

    public E getEndpoint() {
        return endpoint;
    }
}
