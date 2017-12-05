package com.tny.game.net.netty;

import com.tny.game.net.base.AppConfiguration;
import com.tny.game.net.common.AbstractNetTunnel;
import com.tny.game.net.message.DetectMessage;
import com.tny.game.net.session.Session;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public abstract class NettyTunnel<UID> extends AbstractNetTunnel<UID> {

    protected Channel channel;

    public NettyTunnel(Channel channel, AppConfiguration configuration) {
        super(configuration);
        this.channel = channel;
    }

    @Override
    public boolean isConnected() {
        return this.channel.isActive();
    }

    @Override
    public String getHostName() {
        return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
    }

    @Override
    public CompletableFuture<Void> close() {
        CompletableFuture<Void> future = new CompletableFuture<>();
        this.channel.close().addListener(f -> this.handleClose(f, future));
        return future;
    }

    private void handleClose(Future<?> future, CompletableFuture<Void> tunnelFuture) {
        if (future.isSuccess()) {
            sessionOffline();
        }
    }

    private void sessionOffline() {
        Session<UID> session = this.session;
        if (session != null)
            session.offlineIfCurrent(this);
    }

    @Override
    public void ping() {
        this.channel.writeAndFlush(DetectMessage.ping());
    }

    @Override
    public void pong() {
        this.channel.writeAndFlush(DetectMessage.pong());
    }


    @Override
    public boolean isClosed() {
        return !this.channel.isActive();
    }

    @Override
    public String toString() {
        return "NettyTunnel[" + channel + "]";
    }

}
