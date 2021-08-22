package com.tny.game.net.netty4;

import com.tny.game.common.concurrent.*;
import com.tny.game.common.utils.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import org.slf4j.*;

import java.util.Collection;

/**
 * <p>
 */
public class NettyWriteMessagePromise extends AbstractFuture<Void> implements WriteMessagePromise, ChannelFutureListener {

	public static final Logger LOGGER = LoggerFactory.getLogger(NettyWriteMessagePromise.class);

	private volatile ChannelPromise channelPromise;

	private volatile RespondFuture respondFuture;

	private volatile WriteMessageListenerHolder holder;

	private WriteMessageListenerHolder holder() {
		if (this.holder != null) {
			return this.holder;
		}
		synchronized (this) {
			if (this.holder != null) {
				return this.holder;
			}
			this.holder = new WriteMessageListenerHolder();
		}
		return this.holder;
	}

	public NettyWriteMessagePromise() {
	}

	@Override
	public boolean isSuccess() {
		return this.isDone() && this.getRawCause() == null;
	}

	@Override
	public Throwable cause() {
		return this.getRawCause();
	}

	@Override
	public void addWriteListener(WriteMessageListener listener) {
		synchronized (this) {
			this.holder().addWriteListener(listener);
			if (this.isDone()) {
				fireListeners();
			}
		}
	}

	@Override
	public void addWriteListeners(Collection<WriteMessageListener> listeners) {
		synchronized (this) {
			this.holder().addWriteListeners(listeners);
			if (this.isDone()) {
				fireListeners();
			}
		}
	}

	@Override
	public void setRespondFuture(RespondFuture respondFuture) {
		this.respondFuture = respondFuture;
	}

	public boolean channelPromise(ChannelPromise promise) {
		Asserts.checkNotNull(promise, "channelPromise is null");
		if (this.isDone() || this.channelPromise != null) {
			return false;
		}
		synchronized (this) {
			if (this.isDone() || this.channelPromise != null) {
				return false;
			}
			this.channelPromise = promise;
			this.channelPromise.addListener(this);
			return true;
		}
	}

	@Override
	public void success() {
		if (this.isDone()) {
			return;
		}
		synchronized (this) {
			if (this.isDone()) {
				return;
			}
			if (this.channelPromise != null) {
				if (!this.channelPromise.isDone()) {
					this.channelPromise.setSuccess();
				}
			} else {
				this.set(null);
				this.fireListeners();
			}
		}
	}

	private <E extends Throwable> void failed(E cause, boolean throwOut) throws E {
		if (this.isDone()) {
			return;
		}
		synchronized (this) {
			if (this.isDone()) {
				return;
			}
			if (this.channelPromise != null) {
				if (!this.channelPromise.isDone()) {
					this.channelPromise.setFailure(cause);
				}
			} else {
				this.setFailure(cause);
				this.fireListeners();
			}
		}
		if (throwOut) {
			throw cause;
		}
	}

	@Override
	public <E extends Throwable> void failedAndThrow(E cause) throws E {
		this.failed(cause, true);
	}

	@Override
	public <E extends Throwable> void failed(E cause) {
		try {
			this.failed(cause, false);
		} catch (Throwable e) {
			LOGGER.error("", e);
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (this.isDone()) {
			return false;
		}
		synchronized (this) {
			if (this.isDone()) {
				return false;
			}
			if (this.channelPromise != null) {
				return this.channelPromise.cancel(mayInterruptIfRunning);
			} else {
				if (super.cancel(mayInterruptIfRunning)) {
					this.fireListeners();
					return true;
				}
				return false;
			}
		}
	}

	private void fireListeners() {
		if (!this.isDone()) {
			return;
		}
		if (!this.isSuccess() && this.respondFuture != null) {
			this.respondFuture.completeExceptionally(this.getRawCause());
		}
		this.holder().fireListeners(this);
	}

	@Override
	public void operationComplete(ChannelFuture future) {
		synchronized (this) {
			if (future.isSuccess()) {
				super.set(null);
			} else if (future.isCancelled()) {
				super.cancel(true);
			} else if (future.cause() != null) {
				super.setFailure(future.cause());
			}
			this.fireListeners();
		}
	}

}
