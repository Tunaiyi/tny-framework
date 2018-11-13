package com.tny.game.net.netty4;

import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * Created by Kun Yang on 2017/3/28.
 */
public abstract class NettyTunnel<UID> extends AbstractNetTunnel<UID> {

    protected volatile Channel channel;

    protected NettyTunnel(Channel channel, Certificate<UID> certificate, TunnelMode mode, MessageFactory<UID> messageBuilderFactory) {
        super(certificate, mode, messageBuilderFactory);
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
    public SendContext<UID> write(Message<UID> message, MessageContext<UID> context, long waitForSendTimeout, WriteCallback<UID> callback) throws NetException {
        ChannelFuture writeFuture = channel.writeAndFlush(message);
        if (callback != null) {
            writeFuture.addListener((f) -> {
                if (f.isSuccess()) {
                    callback.onWrite(true, null, message, context);
                } else {
                    callback.onWrite(false, f.cause(), message, context);
                }
            });
        }
        if (waitForSendTimeout > 0 && !writeFuture.awaitUninterruptibly(waitForSendTimeout, TimeUnit.MILLISECONDS)) {
            writeFuture.cancel(true);
            throw new SendTimeoutException(format("{} send message timeout {} ms", this, waitForSendTimeout));
        }
        return ifNull(context, EmptySendContext.empty());
    }

    protected NettyTunnel<UID> setChannel(Channel channel) {
        this.channel = channel;
        return this;
    }

}
