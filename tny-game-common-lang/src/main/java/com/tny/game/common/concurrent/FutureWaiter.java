package com.tny.game.common.concurrent;

import java.util.concurrent.Future;

/**
 * Created by Kun Yang on 2017/6/2.
 */
class FutureWaiter<R> implements Waiting<R> {

	public static final int EXECUTE = 1;

	public static final int FAILED = 2;

	public static final int SUCCESS = 3;

	private final Future<R> future;

	private byte state = EXECUTE;

	private volatile Throwable cause;

	private volatile R value;

	FutureWaiter(Future<R> future) {
		this.future = future;
	}

	@Override
	public boolean isDone() {
		if (this.state != EXECUTE) {
			return true;
		}
		this.check();
		return this.state != EXECUTE;
	}

	@Override
	public boolean isFailed() {
		if (this.state == FAILED) {
			return true;
		}
		this.check();
		return this.state == FAILED;
	}

	@Override
	public boolean isSuccess() {
		if (this.state == SUCCESS) {
			return true;
		}
		this.check();
		return this.state == SUCCESS;
	}

	@Override
	public Throwable getCause() {
		if (this.isFailed()) {
			return this.cause;
		}
		this.check();
		return this.cause;
	}

	@Override
	public R getResult() {
		if (this.isSuccess()) {
			return this.value;
		}
		this.check();
		return this.value;
	}

	private void check() {
		if (this.future.isDone()) {
			try {
				this.value = this.future.get();
				this.state = SUCCESS;
			} catch (Throwable e) {
				this.cause = e;
				this.state = FAILED;
			}
		}
	}

}
