package com.tny.game.net.netty4;

import com.tny.game.common.url.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * <p>
 */
public class MockNettyClient extends MockNetEndpoint implements NettyTerminal<Long> {

    private URL url;

    public MockNettyClient(URL url, Certificate<Long> certificate) {
        super(certificate);
        this.url = url;
    }

    @Override
    public long getConnectTimeout() {
        return 0;
    }

    @Override
    public boolean getInitConnectAsyn() {
        return false;
    }

    @Override
    public Channel connect() {
        return new MockChannel(new InetSocketAddress(8090), new InetSocketAddress(8091));
    }

    @Override
    public void reconnectTunnel(NettyTerminalTunnel<Long> tunnel) {

    }

    @Override
    public void connectSuccess(NettyTerminalTunnel<Long> tunnel) {

    }

    @Override
    public URL getUrl() {
        return this.url;
    }

}
