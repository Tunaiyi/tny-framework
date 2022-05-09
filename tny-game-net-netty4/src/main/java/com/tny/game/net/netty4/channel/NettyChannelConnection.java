package com.tny.game.net.netty4.channel;

import com.tny.game.common.context.*;
import com.tny.game.net.netty4.network.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:46 下午
 */
public abstract class NettyChannelConnection extends AttributesHolder implements Connection {

    protected Channel channel;

    private final AtomicBoolean close = new AtomicBoolean(false);

    protected NettyChannelConnection(Channel channel) {
        this.channel = channel;
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
    public boolean isClosed() {
        return close.get();
    }

    @Override
    public boolean close() {
        if (close.get()) {
            return false;
        }
        if (close.compareAndSet(false, true)) {
            this.doClose();
            this.channel.disconnect();
            return true;
        }
        return false;
    }

    protected void doClose() {
    }

    protected ChannelPromise createChannelPromise(MessageWriteAwaiter awaiter) {
        ChannelPromise channelPromise = this.channel.newPromise();
        if (awaiter != null) {
            channelPromise.addListener(new NettyWriteMessageHandler(awaiter));
        }
        return channelPromise;
    }

    @Override
    public String toString() {
        return valueOf(this.channel);
    }

}
