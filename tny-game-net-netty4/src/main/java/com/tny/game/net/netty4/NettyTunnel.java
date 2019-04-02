package com.tny.game.net.netty4;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.concurrent.locks.Lock;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public abstract class NettyTunnel<UID, E extends NetEndpoint<UID>> extends AbstractNetTunnel<UID, E> {

    protected volatile Channel channel;

    protected NettyTunnel(Channel channel, TunnelMode mode, E endpoint, MessageFactory<UID> messageFactory) {
        super(mode, endpoint, messageFactory);
        this.channel = channel;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return ((InetSocketAddress) channel.remoteAddress());
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return ((InetSocketAddress) channel.localAddress());
    }

    @Override
    public boolean isAvailable() {
        return this.getState() == TunnelState.ACTIVATE && this.channel != null && this.channel.isActive();
    }

    Channel getChannel() {
        return channel;
    }

    protected Lock getLock() {
        return this.lock;
    }

    @Override
    protected void doDisconnect() {
        Channel channel = this.channel;
        if (channel != null && channel.isActive()) {
            channel.close();
        }
    }

    @Override
    protected void onClose() {
        if (this.channel.isActive()) {
            try {
                this.channel.close();
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    @Override
    public WriteMessageFuture write(Message<UID> message, WriteMessagePromise promise) throws NetException {
        if (promise == null)
            promise = createWritePromise(-1);
        if (!this.isAvailable()) {
            promise.failed(new TunnelCloseException(format("{} is close {}", this)));
            return promise;
        }
        if (promise instanceof NettyWriteMessagePromise) {
            channel.writeAndFlush(message, as(promise));
            return promise;
        } else {
            throw new TunnelException("Cannot support {} WriteMessageFuture", promise.getClass());
        }
    }

    @Override
    public WriteMessagePromise createWritePromise(long sendTimeout) {
        return new NettyWriteMessagePromise(this.channel, sendTimeout);
    }

    protected NettyTunnel<UID, E> setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

}
