package com.tny.game.net.netty4.network;

import com.tny.game.net.endpoint.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import io.netty.util.concurrent.FastThreadLocalThread;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/12 8:31 下午
 */
public class NettyMessageBearer implements Runnable {

    private final MessageAllocator maker;

    private final MessageFactory factory;

    private final MessageContext context;

    private volatile Message message;

    private final Channel channel;

    private final ChannelPromise promise;

    public NettyMessageBearer(Channel channel, MessageAllocator maker, MessageFactory factory, MessageContext context, ChannelPromise promise) {
        this.maker = maker;
        this.factory = factory;
        this.context = context;
        this.channel = channel;
        this.promise = promise;
    }

    Message message() {
        Thread thread = Thread.currentThread();
        if (thread instanceof FastThreadLocalThread) {
            if (this.message != null) {
                return this.message;
            }
            if (this.context != null) {
                this.message = this.maker.allocate(this.factory, this.context);
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
