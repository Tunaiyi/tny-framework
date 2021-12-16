package com.tny.game.net.netty4.channel;

import com.tny.game.net.netty4.network.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;

import java.net.InetSocketAddress;

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
