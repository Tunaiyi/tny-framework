package com.tny.game.net.transport;

import org.slf4j.*;

import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/18 3:11 下午
 */
public class WriteMessageEventBus {

	public static final Logger LOGGER = LoggerFactory.getLogger(WriteMessageEventBus.class);

	private volatile List<WriteMessageListener> listeners;

	private List<WriteMessageListener> listeners() {
		if (this.listeners != null) {
			return this.listeners;
		}
		synchronized (this) {
			if (this.listeners != null) {
				return this.listeners;
			}
			this.listeners = new LinkedList<>();
		}
		return this.listeners;
	}

	public List<WriteMessageListener> getListeners() {
		if (this.listeners == null) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableList(this.listeners);
	}

	public void addWriteListener(WriteMessageListener listener) {
		listeners().add(listener);
	}

	public void addWriteListeners(Collection<WriteMessageListener> listeners) {
		listeners().addAll(listeners);
	}

	public void fireListeners(WriteMessageFuture future) {
		List<WriteMessageListener> listeners = this.listeners;
		if (listeners != null) {
			for (WriteMessageListener listener : listeners) {
				fireListener(listener, future);
			}
			listeners.clear();
		}
	}

	private void fireListener(WriteMessageListener listener, WriteMessageFuture future) {
		try {
			listener.onWrite(future);
		} catch (Throwable e) {
			LOGGER.error("fire {} exception", listener, e);
		}
	}

}
