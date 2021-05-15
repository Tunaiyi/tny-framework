package com.tny.game.net.netty4.passthrough;

import com.tny.game.net.message.*;
import com.tny.game.net.passthrough.*;
import com.tny.game.net.transport.*;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/4/30 5:51 下午
 */
public class NettyPipe<UID> extends BaseNetPipe<UID> {

    private final Channel channel;

    public NettyPipe(Channel channel) {
        this.channel = channel;
    }

    @Override
    protected void onClose() {
        if (this.channel.isActive()) {
            this.channel.close();
        }
    }

    @Override
    protected boolean isConnected() {
        return this.channel.isActive();
    }

    @Override
    public void write(NetTubule<UID> tubule, Message message, WriteMessagePromise promise) {

    }

    @Override
    public WriteMessagePromise createWritePromise(long timeout) {
        return null;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
    }

}
