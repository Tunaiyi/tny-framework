package com.tny.game.net.netty;

import com.tny.game.common.concurrent.StageableFuture;
import com.tny.game.net.transport.*;
import com.tny.game.net.transport.message.MessageBuilderFactory;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public abstract class NettyTunnel<UID> extends AbstractNetTunnel<UID> {

    protected Channel channel;

    protected NettyTunnel(Channel channel, Certificate<UID> certificate) {
        super(certificate);
        this.channel = channel;
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
        try {
            this.channel.close().addListener(f -> this.handleClose(f, future.future()));
        } catch (Throwable e) {
            LOGGER.error("", e);
            future.future().completeExceptionally(e);
        }
        return future;
    }

    private void handleClose(Future<?> future, CompletableFuture<Void> tunnelFuture) {
        if (future.isSuccess()) {
            // NetSession<UID> session = this.session;
            // if (session != null)
            //     session.offlineIfCurrent(this);
            tunnelFuture.complete(null);
            this.destroyFutureHolder();
        } else {
            tunnelFuture.completeExceptionally(future.cause());
        }
    }

    @Override
    public boolean isClosed() {
        return !this.channel.isActive();
    }

    Channel getChannel() {
        return channel;
    }

    @Override
    protected NettyTunnel<UID> setMessageBuilderFactory(MessageBuilderFactory<UID> messageBuilderFactory) {
        super.setMessageBuilderFactory(messageBuilderFactory);
        return this;
    }

    @Override
    protected NettyTunnel<UID> setInputEventHandler(MessageInputEventHandler<UID, NetTunnel<UID>> inputEventHandler) {
        super.setInputEventHandler(inputEventHandler);
        return this;
    }

    @Override
    protected NettyTunnel<UID> setOutputEventHandler(MessageOutputEventHandler<UID, NetTunnel<UID>> outputEventHandler) {
        super.setOutputEventHandler(outputEventHandler);
        return this;
    }

    protected NettyTunnel<UID> setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

}
