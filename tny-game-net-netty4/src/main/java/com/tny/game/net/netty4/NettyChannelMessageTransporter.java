package com.tny.game.net.netty4;

import com.tny.game.common.runtime.*;
import com.tny.game.net.base.*;
import com.tny.game.net.endpoint.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import org.slf4j.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.net.netty4.NettyNetAttrKeys.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/19 2:26 下午
 */
public class NettyChannelMessageTransporter<UID> extends NettyChannelConnection implements MessageTransporter<UID> {

	public static final Logger LOGGER = LoggerFactory.getLogger(NettyChannelMessageTransporter.class);

	public NettyChannelMessageTransporter(Channel channel) {
		super(channel);
	}

	public Channel getChannel() {
		return this.channel;
	}

	@Override
	public WriteMessageFuture write(Message message, WriteMessagePromise promise) throws NetException {
		ChannelPromise channelPromise = checkAndCreateChannelPromise(promise);
		this.channel.writeAndFlush(message, channelPromise);
		return promise;
	}

	@Override
	public WriteMessageFuture write(MessageAllocator maker, MessageFactory factory, MessageContext context) throws NetException {
		WriteMessagePromise promise = as(context.getWriteMessageFuture());
		ChannelPromise channelPromise = checkAndCreateChannelPromise(promise);
		ProcessTracer tracer = NetLogger.NET_TRACE_OUTPUT_WRITE_TO_ENCODE_WATCHER.trace();
		this.channel.eventLoop()
				.execute(() -> {
					Message message = maker.allocate(factory, context);
					this.channel.writeAndFlush(message, channelPromise);
					tracer.done();
				});
		return promise;
	}

	@Override
	public void close() {
		NetTunnel<UID> tunnel = as(this.channel.attr(TUNNEL).getAndSet(null));
		if (tunnel != null && (tunnel.isOpen() || tunnel.isActive())) {
			tunnel.disconnect();
		}
		this.channel.disconnect();
	}

	@Override
	public void bind(NetTunnel<UID> tunnel) {
		this.channel.attr(TUNNEL).set(tunnel);
	}

	@Override
	public WriteMessagePromise createWritePromise() {
		return new NettyWriteMessagePromise();
	}

}
