package com.tny.game.net.netty;

import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import com.tny.game.net.transport.message.*;
import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static com.tny.game.common.utils.StringAide.*;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public abstract class NettyTunnel<UID> extends AbstractNetTunnel<UID> {

    protected Channel channel;

    private volatile TunnelState state = TunnelState.INIT;

    protected NettyTunnel(Channel channel, Certificate<UID> certificate, TunnelMode mode) {
        super(certificate, mode);
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
    public boolean isClosed() {
        return state == TunnelState.CLOSE;
    }

    @Override
    public boolean isActive() {
        return (this.state == TunnelState.CLOSE || this.channel != null && this.channel.isActive());
    }

    Channel getChannel() {
        return channel;
    }

    @Override
    public void open() throws ValidatorFailException {
        if (state != TunnelState.INIT)
            return;
        synchronized (this) {
            if (state != TunnelState.INIT)
                return;
            if (this.channel.isActive()) {
                this.state = TunnelState.OPEN;
                this.openEvent().notify(this);
            } else {
                throw new TunnelException("channel {} is close", this.channel);
            }
        }
    }

    @Override
    public void close() {
        if (state == TunnelState.CLOSE)
            return;
        synchronized (this) {
            if (state == TunnelState.CLOSE)
                return;
            if (this.channel.isActive()) {
                try {
                    this.state = TunnelState.CLOSE;
                    this.channel.close();
                    this.getBindSession().ifPresent(s -> s.discardTunnel(this));
                    this.destroyFutureHolder();
                    this.closeEvent().notify(this);
                } catch (Throwable e) {
                    LOGGER.error("", e);
                }
            }
        }
    }

    @Override
    protected NettyTunnel<UID> setMessageFactory(MessageFactory<UID> messageFactory) {
        super.setMessageFactory(messageFactory);
        return this;
    }

    @Override
    protected NettyTunnel<UID> setMessageHandler(MessageHandler<UID> messageHandler) {
        super.setMessageHandler(messageHandler);
        return this;
    }

    protected NettyTunnel<UID> setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

    @Override
    protected MessageHandler<UID> getMessageHandler() {
        return super.getMessageHandler();
    }

    @Override
    protected void doWrite(Message<UID> message) {
        channel.writeAndFlush(message);
    }

    @Override
    protected void doWrite(MessageSubject subject, MessageContext<UID> context, long waitForSendTimeout) throws NetException {
        ChannelFuture writeFuture = null;
        NetMessage<UID> message = this.messageBuilderFactory.create(0, subject, this.getCertificate());
        message.setId(createMessageID());
        if (this.session != null)
            this.session.addSentMessage(message);
        if (this.isActive())
            writeFuture = channel.writeAndFlush(message);
        if (context == null)
            return;
        if (context.isHasFuture()) {
            if (writeFuture == null) {
                NetException cause = new NetException("{} channel is close");
                this.completeExceptionally(context, cause);
                if (waitForSendTimeout > 0)
                    throw cause;
            } else {
                writeFuture.addListener((f) -> {
                    if (f.isSuccess()) {
                        this.completeFuture(context.getSendFuture(), message);
                        this.registerFuture(message.getId(), context.getRespondFuture());
                    } else {
                        this.completeExceptionally(context, f.cause());
                    }
                });
                if (waitForSendTimeout > 0 && !writeFuture.awaitUninterruptibly(waitForSendTimeout, TimeUnit.MILLISECONDS)) {
                    writeFuture.cancel(true);
                    throw new SendTimeoutException(format("{} send message timeout {} ms", this, waitForSendTimeout));
                }
            }
        }
    }

}
