package com.tny.game.net.transport;

import com.tny.game.common.concurrent.*;
import org.slf4j.*;

import java.util.Collection;

/**
 * <p>
 */
public class DefaultWriteMessagePromise extends AbstractFuture<Void> implements WriteMessagePromise {

	public static final Logger LOGGER = LoggerFactory.getLogger(DefaultWriteMessagePromise.class);

	private volatile RespondFuture respondFuture;

	private volatile WriteMessageEventBus event;

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

	public DefaultWriteMessagePromise() {
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

	@Override
	public void success() {
		if (this.isDone()) {
			return;
		}
		synchronized (this) {
			if (this.isDone()) {
				return;
			}
			this.set(null);
			this.fireListeners();
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
			this.setFailure(cause);
			this.fireListeners();
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
			if (super.cancel(mayInterruptIfRunning)) {
				this.fireListeners();
				return true;
			}
			return false;
		}
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

}
