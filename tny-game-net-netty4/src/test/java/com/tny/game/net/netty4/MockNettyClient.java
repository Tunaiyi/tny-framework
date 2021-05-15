package com.tny.game.net.netty4;

import com.tny.game.common.url.*;
import com.tny.game.net.endpoint.*;
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
    public boolean isAsyncConnect() {
        return false;
    }

    @Override
    public Channel connect() {
        return new MockChannel(new InetSocketAddress(8090), new InetSocketAddress(8091));
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
    public void connectSuccess(NettyClientTunnel<Long> tunnel) {

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
