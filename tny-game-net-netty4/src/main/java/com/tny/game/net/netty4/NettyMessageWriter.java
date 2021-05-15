package com.tny.game.net.netty4;

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
class NettyMessageWriter<UID> implements Runnable {

    private final MessageCreator<UID> creator;
    private final MessageContext<UID> context;
    private volatile Message message;
    private final Channel channel;
    private final ChannelPromise promise;

    public NettyMessageWriter(Channel channel, MessageCreator<UID> creator, MessageContext<UID> context, ChannelPromise promise) {
        this.creator = creator;
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
            return this.message = this.creator.createMessage(this.context);
        }
        throw new IllegalArgumentException(
                format("currentThread {} is not {}", thread.getName(), FastThreadLocalThread.class));
    }

    @Override
    public void run() {
        this.channel.writeAndFlush(this.message(), this.promise);
    }

}
