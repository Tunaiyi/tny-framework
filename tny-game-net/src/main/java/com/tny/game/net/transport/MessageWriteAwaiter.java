package com.tny.game.net.transport;

import com.tny.game.common.concurrent.*;

import java.util.concurrent.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2019-03-15 21:13
 */

public class MessageWriteAwaiter extends CompletableFuture<Void> implements StageFuture<Void> {

	private volatile MessageRespondAwaiter respondAwaiter;

	public void respondFuture(MessageRespondAwaiter awaiter) {
		this.respondAwaiter = awaiter;
		if (this.isDone() || this.respondAwaiter != null) {
			return;
		}
		synchronized (this) {
			if (this.isDone() || this.respondAwaiter != null) {
				return;
			}
			this.respondAwaiter = awaiter;
			this.whenComplete((v, cause) -> {
				if (cause != null) {
					if (cause instanceof CancellationException) {
						this.respondAwaiter.cancel(false);
					} else {
						this.respondAwaiter.completeExceptionally(cause);
					}
				}
			});
		}
	}

}
