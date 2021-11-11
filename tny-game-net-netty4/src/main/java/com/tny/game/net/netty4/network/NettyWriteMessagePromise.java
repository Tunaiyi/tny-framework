package com.tny.game.net.netty4.network;

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

	private volatile WriteMessageEventBus event;

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
			this.event().addWriteListener(listener);
			if (this.isDone()) {
				fireListeners();
			}
		}
	}

	@Override
	public void addWriteListeners(Collection<WriteMessageListener> listeners) {
		synchronized (this) {
			this.event().addWriteListeners(listeners);
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
				doSuccess();
			}
		}
	}

	private void doSuccess() {
		this.set(null);
		this.fireListeners();
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
				doFailed(cause);
			}
		}
		if (throwOut) {
			throw cause;
		}
	}

	private void doFailed(Throwable cause) {
		this.setFailure(cause);
		this.fireListeners();
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
				return this.doCancel(mayInterruptIfRunning);
			}
		}
	}

	private boolean doCancel(boolean mayInterruptIfRunning) {
		if (super.cancel(mayInterruptIfRunning)) {
			this.fireListeners();
			return true;
		}
		return false;
	}

	private void fireListeners() {
		if (!this.isDone()) {
			return;
		}
		if (!this.isSuccess() && this.respondFuture != null) {
			this.respondFuture.completeExceptionally(this.getRawCause());
		}
		WriteMessageEventBus eventBus = this.event;
		if (eventBus != null) {
			eventBus.fireListeners(this);
		}
	}

	@Override
	public void operationComplete(ChannelFuture future) {
		synchronized (this) {
			if (future.isSuccess()) {
				this.doSuccess();
			} else if (future.isCancelled()) {
				this.cancel(true);
			} else if (future.cause() != null) {
				this.doFailed(future.cause());
			}
		}
	}

	private WriteMessageEventBus event() {
		if (this.event != null) {
			return this.event;
		}
		synchronized (this) {
			if (this.event != null) {
				return this.event;
			}
			this.event = new WriteMessageEventBus();
		}
		return this.event;
	}

}
