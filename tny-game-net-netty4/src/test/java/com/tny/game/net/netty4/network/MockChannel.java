package com.tny.game.net.netty4.network;

import io.netty.channel.embedded.EmbeddedChannel;

import java.net.SocketAddress;

/**
 * <p>
 */
public class MockChannel extends EmbeddedChannel {

    private volatile SocketAddress localAddress;

    private volatile SocketAddress remoteAddress;

    public MockChannel(SocketAddress localAddress, SocketAddress remoteAddress) {
        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    @Override
    public SocketAddress localAddress() {
        return isActive() ? localAddress : null;
    }

    @Override
    public SocketAddress remoteAddress() {
        return isActive() ? remoteAddress : null;
    }

}
