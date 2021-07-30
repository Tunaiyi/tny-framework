package com.tny.game.net.netty4;

import com.tny.game.net.exception.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;

import java.net.InetSocketAddress;

import static com.tny.game.common.utils.ObjectAide.*;
import static java.lang.String.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/21 3:46 下午
 */
public abstract class NettyChannelConnection implements Connection {

    protected Channel channel;

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

    protected NettyChannelConnection(Channel channel) {
        this.channel = channel;
    }

    protected ChannelPromise checkAndCreateChannelPromise(WriteMessagePromise promise) {
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
    public String toString() {
        return valueOf(this.channel);
    }

}
