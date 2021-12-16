package com.tny.game.net.netty4.network;

import com.tny.game.net.transport.*;
import io.netty.channel.*;
import org.slf4j.*;

/**
 * <p>
 */
public class NettyWriteMessageHandler implements ChannelFutureListener {

	public static final Logger LOGGER = LoggerFactory.getLogger(NettyWriteMessageHandler.class);

	private final MessageWriteAwaiter awaiter;

	public NettyWriteMessageHandler(MessageWriteAwaiter awaiter) {
		this.awaiter = awaiter;
	}

	@Override
	public void operationComplete(ChannelFuture future) {
		synchronized (this) {
			if (future.isSuccess()) {
				this.awaiter.complete(null);
			} else if (future.isCancelled()) {
				this.awaiter.cancel(true);
			} else if (future.cause() != null) {
				this.awaiter.completeExceptionally(future.cause());
			}
		}
	}

}
