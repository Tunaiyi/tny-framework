package com.tny.game.net.netty4;

import com.tny.game.common.runtime.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import io.netty.channel.*;
import io.netty.util.concurrent.FastThreadLocalThread;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/12 8:31 下午
 */
class NettyMessageBearer<UID> implements Runnable {

    private final MessageMaker<UID> maker;
    private final MessageContext<UID> context;
    private volatile Message message;
    private final Channel channel;
    private final ChannelPromise promise;
    private final ProcessTracer tracer;

    public NettyMessageBearer(Channel channel, MessageMaker<UID> maker, MessageContext<UID> context, ChannelPromise promise,
            ProcessTracer tracer) {
        this.maker = maker;
        this.context = context;
        this.channel = channel;
        this.promise = promise;
        this.tracer = tracer;
    }

    Message message() {
        Thread thread = Thread.currentThread();
        if (thread instanceof FastThreadLocalThread) {
            if (this.message != null) {
                if (this.message.getId() == -1) {
                    if (this.tracer != null) {
                        this.tracer.done();
                    }
                }
                return this.message;
            }
            if (this.context != null) {
                if (this.tracer != null) {
                    this.tracer.done();
                }
                this.message = this.maker.newMessage(this.context);
                return this.message;
            }
        }
        throw new IllegalArgumentException(format("currentThread {} is not {}", thread.getName(), FastThreadLocalThread.class));
    }

    @Override
    public void run() {
        this.channel.writeAndFlush(this.message(), this.promise);
    }

}
