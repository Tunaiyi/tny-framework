package com.tny.game.net.netty;

import com.tny.game.common.concurrent.StageableFuture;
import com.tny.game.net.common.AbstractNetTunnel;
import com.tny.game.net.message.*;
import com.tny.game.net.session.*;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public abstract class NettyTunnel<UID> extends AbstractNetTunnel<UID> {

    protected Channel channel;

    protected NettyTunnel(Channel channel) {
        super();
        this.channel = channel;
    }

    protected NettyTunnel(Channel channel, SessionFactory<UID> sessionFactory, MessageBuilderFactory<UID> messageBuilderFactory) {
        super();
        this.channel = channel;
        this.init(sessionFactory, messageBuilderFactory);
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return ((InetSocketAddress) channel.remoteAddress());
    }

    @Override
    public InetSocketAddress localAddress() {
        return ((InetSocketAddress) channel.localAddress());
    }

    @Override
    public StageableFuture<Void> close() {
        CommonStageableFuture<Void> future = CommonStageableFuture.createFuture();
        this.channel.close().addListener(f -> this.handleClose(f, future.future()));
        return future;
    }

    private void handleClose(Future<?> future, CompletableFuture<Void> tunnelFuture) {
        if (future.isSuccess()) {
            sessionOffline();
            tunnelFuture.complete(null);
        }
    }

    private void sessionOffline() {
        NetSession<UID> session = this.session;
        if (session != null)
            session.offlineIfCurrent(this);
    }

    @Override
    public boolean isClosed() {
        return !this.channel.isActive();
    }

    Channel getChannel() {
        return channel;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", NettyTunnel.class.getSimpleName() + "[", "]")
                .add("channel=" + channel)
                .add("session=" + session)
                .toString();
    }
}
