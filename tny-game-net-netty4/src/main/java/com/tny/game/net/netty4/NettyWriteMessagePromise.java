package com.tny.game.net.netty4;

import com.tny.game.net.transport.*;
import io.netty.channel.*;
import io.netty.util.concurrent.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2019-03-15 21:16
 */
public class NettyWriteMessagePromise extends DefaultChannelPromise implements WriteMessagePromise {

    private long timeout;

    public NettyWriteMessagePromise(Channel channel, long timeout) {
        super(channel);
    }

    public NettyWriteMessagePromise(Channel channel, long timeout, EventExecutor executor) {
        super(channel, executor);
    }

    @Override
    public long getWriteTimeout() {
        return timeout;
    }

    @Override
    public void addWriteListener(WriteMessageListener listener) {
        GenericFutureListener<Future<Void>> nettyListener = (f) -> listener.onWrite(this);
        this.addListener(nettyListener);
    }

    @Override
    public void success() {
        this.setSuccess();
    }

    @Override
    public void failed(Throwable cause) {
        this.setFailure(cause);
    }
}
