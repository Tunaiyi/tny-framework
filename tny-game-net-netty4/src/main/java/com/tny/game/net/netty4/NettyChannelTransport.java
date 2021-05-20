package com.tny.game.net.netty4;

import com.tny.game.common.runtime.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import org.slf4j.*;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.function.Supplier;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.netty4.NettyAttrKeys.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/19 2:26 下午
 */
public class NettyChannelTransport<UID> implements NetTransport<UID> {

    public static final Logger LOGGER = LoggerFactory.getLogger(NettyChannelTransport.class);

    private final Channel channel;

    public NettyChannelTransport(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public WriteMessageFuture write(Message message, WriteMessagePromise promise) throws NetException {
        ChannelPromise channelPromise = checkAndCreateChannelPromise(promise);
        this.channel.writeAndFlush(message, channelPromise);
        return promise;
    }

    @Override
    public WriteMessageFuture write(MessageMaker<UID> maker, MessageContext<UID> context) throws NetException {
        WriteMessagePromise promise = as(context.getWriteMessageFuture());
        ChannelPromise channelPromise = checkAndCreateChannelPromise(promise);
        ProcessTracer tracer = NetLogger.NET_TRACE_OUTPUT_WRITE_TO_ENCODE_WATCHER.trace();
        //        this.channel.writeAndFlush(new NettyMessageBearer<>(this.channel, maker, context, channelPromise, tracer), channelPromise);
        this.channel.eventLoop()
                .execute(() -> {
                    this.channel.writeAndFlush(maker.newMessage(context), channelPromise);
                    tracer.done();
                });
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

    private ChannelPromise checkAndCreateChannelPromise(WriteMessagePromise promise) {
        if (promise != null && !(promise instanceof NettyWriteMessagePromise)) {
            promise.failedAndThrow(new TunnelException("Cannot support {}", promise.getClass()));
        }
        ChannelPromise channelPromise = this.channel.newPromise();
        if (promise != null) {
            NettyWriteMessagePromise messagePromise = as(promise);
            if (!messagePromise.channelPromise(channelPromise)) {
                messagePromise.failedAndThrow(new TunnelException("WriteMessageFuture {} is done", messagePromise));
            }
        }
        return channelPromise;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress)this.channel.remoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress)this.channel.localAddress();
    }

    @Override
    public boolean isActive() {
        return this.channel.isActive();
    }

    @Override
    public void close() {
        this.channel.close();
    }

    @Override
    public void bind(NetTunnel<UID> tunnel) {
        this.channel.attr(TUNNEL).set(tunnel);
    }

    @Override
    public WriteMessagePromise createWritePromise(long sendTimeout) {
        return new NettyWriteMessagePromise(sendTimeout);
    }

}
