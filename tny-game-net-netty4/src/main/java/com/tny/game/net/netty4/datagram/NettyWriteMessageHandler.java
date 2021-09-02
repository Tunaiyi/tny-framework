package com.tny.game.net.netty4.datagram;

import com.tny.game.net.transport.*;
import io.netty.channel.*;
import org.slf4j.*;

/**
 * <p>
 */
public class NettyWriteMessageHandler implements ChannelFutureListener {

	public static final Logger LOGGER = LoggerFactory.getLogger(NettyWriteMessageHandler.class);

	private final WriteMessagePromise promise;

	public NettyWriteMessageHandler(WriteMessagePromise promise) {
		this.promise = promise;
	}

	@Override
	public void operationComplete(ChannelFuture future) {
		synchronized (this) {
			if (future.isSuccess()) {
				this.promise.success();
			} else if (future.isCancelled()) {
				this.promise.cancel(true);
			} else if (future.cause() != null) {
				this.promise.failed(future.cause());
			}
		}
	}

}
