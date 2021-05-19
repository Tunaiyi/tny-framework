package com.tny.game.net.netty4;

import com.tny.game.common.runtime.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.function.Supplier;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public abstract class NettyTunnel<UID, E extends NetEndpoint<UID>> extends AbstractTunnel<UID, E> {

    protected volatile Channel channel;

    protected NettyTunnel(Channel channel, TunnelMode mode, NetBootstrapContext<UID> bootstrapContext) {
        super(NetAide.newTunnelId(), mode, bootstrapContext);
        this.channel = channel;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return ((InetSocketAddress)this.channel.remoteAddress());
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return ((InetSocketAddress)this.channel.localAddress());
    }

    @Override
    public boolean isAvailable() {
        return this.getStatus() == TunnelStatus.ACTIVATED && this.channel != null && this.channel.isActive();
    }

    Channel getChannel() {
        return this.channel;
    }

    protected void disconnectChannel() {
        Channel channel = this.channel;
        if (channel != null && channel.isActive()) {
            try {
                channel.close();
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    @Override
    protected void onClose() {
        this.disconnectChannel();
    }

    protected void onWriteUnavailable(MessageContent content, WriteMessagePromise promise) {
    }

    @Override
    public WriteMessageFuture write(MessageMaker<UID> maker, MessageContext<UID> context) throws NetException {
        WriteMessagePromise promise = as(context.getWriteMessageFuture());
        ChannelPromise channelPromise = checkAndCreateChannelPromise(context, promise);
        ProcessTracer tracer = NetLogger.NET_TRACE_OUTPUT_WRITE_TO_ENCODE_WATCHER.trace();
        this.channel.eventLoop()
                .execute(new NettyMessageBearer<>(this.channel, maker, context, channelPromise, tracer));
        return promise;
    }

    @Override
    public WriteMessageFuture write(Message message, WriteMessagePromise promise) throws NetException {
        ChannelPromise channelPromise = checkAndCreateChannelPromise(message, promise);
        this.channel.writeAndFlush(message, channelPromise);
        return promise;
    }

    @Override
    public void write(Supplier<Collection<Message>> messageSupplier) {
        this.channel.eventLoop().execute(() -> {
            Collection<Message> messages = messageSupplier.get();
            for (Message message : messages) {
                try {
                    this.channel.writeAndFlush(message);
                } catch (Throwable e) {
                    LOGGER.error("resent message {} exception", message, e);
                }
            }
        });
    }

    private ChannelPromise checkAndCreateChannelPromise(MessageContent context, WriteMessagePromise promise) {
        if (!this.isAvailable()) {
            this.onWriteUnavailable(context, promise);
            if (promise != null) {
                failAndThrow(promise, new TunnelDisconnectException(format("{} is disconnect {}", this)));
            }
        }
        if (promise != null && !(promise instanceof NettyWriteMessagePromise)) {
            failAndThrow(promise, new TunnelException("Cannot support {} WriteMessageFuture", promise.getClass()));
        }
        ChannelPromise channelPromise = this.channel.newPromise();
        if (promise != null) {
            NettyWriteMessagePromise messagePromise = as(promise);
            if (!messagePromise.channelPromise(channelPromise)) {
                failAndThrow(messagePromise, new TunnelException("WriteMessageFuture {} is done", messagePromise));
            }
        }
        return channelPromise;
    }

    private void failAndThrow(WriteMessagePromise promise, NetException exception) throws NetException {
        promise.failed(exception);
        throw exception;
    }

    @Override
    public WriteMessagePromise createWritePromise(long sendTimeout) {
        return new NettyWriteMessagePromise(sendTimeout);
    }

    protected NettyTunnel<UID, E> setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

    protected AbstractTunnel<UID, E> setEndpoint(E endpoint) {
        this.endpoint = endpoint;
        return this;
    }

}
