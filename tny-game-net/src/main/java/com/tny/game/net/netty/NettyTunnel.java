package com.tny.game.net.netty;

import com.tny.game.net.common.AbstractTunnel;
import com.tny.game.net.message.DetectMessage;
import com.tny.game.net.session.Session;
import com.tny.game.net.session.SessionFactory;
import com.tny.game.net.tunnel.Tunnel;
import com.tny.game.net.tunnel.TunnelContent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public class NettyTunnel<UID> extends AbstractTunnel<UID> {

    private Channel channel;

    public NettyTunnel(Channel channel, SessionFactory<UID> sessionFactory) {
        super(sessionFactory);
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
    public boolean close() {
        if (this.channel.isActive()) {
            this.channel.disconnect().addListener(this::handleClose);
            return true;
        } else {
            sessionOffline();
        }
        return false;
    }

    private void handleClose(Future<?> future) {
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
    public void write(TunnelContent<UID> content) {
        ChannelFuture future = channel.writeAndFlush(content.getMessage());
        CompletableFuture<Tunnel<UID>> sentFuture = content.getSendFuture();
        if (sentFuture != null) {
            future.addListener(f -> {
                if (f.isSuccess())
                    sentFuture.complete(this);
                else
                    sentFuture.completeExceptionally(f.cause());
            });
        }
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
