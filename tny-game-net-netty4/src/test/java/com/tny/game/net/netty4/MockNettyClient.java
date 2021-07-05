package com.tny.game.net.netty4;

import com.tny.game.common.url.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.transport.*;

import java.net.InetSocketAddress;

/**
 * <p>
 */
public class MockNettyClient extends MockNetEndpoint implements NetTerminal<Long> {

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
    public boolean isAsyncConnect() {
        return false;
    }

    @Override
    public Transporter<Long> connect() {
        return new NettyChannelTransporter<>(new MockChannel(new InetSocketAddress(8090), new InetSocketAddress(8091)));
    }

    @Override
    public int getConnectRetryTimes() {
        return 0;
    }

    @Override
    public long getConnectRetryInterval() {
        return 0;
    }

    @Override
    public void reconnect() {

    }

    @Override
    public void onConnected(NetTunnel<Long> tunnel) {

    }

    @Override
    public URL getUrl() {
        return this.url;
    }

    @Override
    public void resend(NetTunnel<Long> tunnel, long fromId, FilterBound bound) {

    }

    @Override
    public void resend(NetTunnel<Long> tunnel, long fromId, long toId, FilterBound bound) {

    }

}
