package com.tny.game.actor;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 响应的未来对象
 * @author KGTny
 *
 * @param <V>
 */
public abstract class Answer<V> {

	/**
	 * 同步对象
	 */
	private Sync<V> sync = new Sync<>();

	public volatile List<AnswerListener<V>> listeners;

	/**
	 * 请求是否取消
	 * @return 取消返回true 否则返回false
	 */
	public boolean isCancelled() {
		return sync.isCancelled();
	}

	/**
	 * 请求是否完成
	 * @return 完成返回true 否则返回false
	 */
	public boolean isDone() {
		return sync.isDone();
	}

	/**
	 * 添加未来响应监听器
	 * @param listener
	 */
	public void addListener(AnswerListener<V> listener) {
		if (this.listeners == null)
			this.listeners = new LinkedList<>();
		this.listeners.add(listener);
	}

	/**
	 * 设置成功结果值
	 * @param result 结果值
	 * @return 返回是否成功
	 */
	protected boolean success(V result) {
		return sync.set(result);
	}

	/**
	 * 设置失败原因
	 * @param throwable 失败原因
	 * @return 设置成功
	 */
	protected boolean fail(Throwable throwable) {
		return sync.setException(throwable);
	}

	/**
	 * 取消请求
	 * @param interrupt 如果正在执行是否中断线程
	 * @return 成功取消返回true 否则返回false
	 */
	protected boolean cancel(boolean interrupt) {
		return sync.cancel(interrupt);
	}

	/**
	 * 非阻塞获取结果值
	 * @return 返回结果值Optional
	 */
	public V result() {
		if (this.isDone())
			return sync.value();
		return null;
	}

	/**
	 * 同步对象类
	 * @author KGTny
	 *
	 * @param <V>
	 */
	static final class Sync<V> extends AbstractQueuedSynchronizer {

		private static final long serialVersionUID = 0L;

		/* Valid states. */
		static final int RUNNING = 0;
		static final int COMPLETING = 1;
		static final int COMPLETED = 2;
		static final int CANCELLED = 4;
		static final int INTERRUPTED = 8;

		private V value;
		private Throwable exception;

		/*
		 * Acquisition succeeds if the future is done, otherwise it fails.
		 */
		@Override
		protected int tryAcquireShared(int ignored) {
			if (isDone()) {
				return 1;
			}
			return -1;
		}

		/*
		 * We always allow a release to go through, this means the state has been
		 * successfully changed and the result is available.
		 */
		@Override
		protected boolean tryReleaseShared(int finalState) {
			setState(finalState);
			return true;
		}

		/**
		 * Blocks until the task is complete or the timeout expires.  Throws a
		 * {@link TimeoutException} if the timer expires, otherwise behaves like
		 * {@link #get()}.
		 */
		V get(long nanos) throws TimeoutException, CancellationException,
				ExecutionException, InterruptedException {
			// Attempt to acquire the shared lock with a timeout.
			if (!tryAcquireSharedNanos(-1, nanos)) {
				throw new TimeoutException("Timeout waiting for task.");
			}
			return getValue();
		}

		V value() {
			return value;
		}

		/**
		 * Blocks until {@link #complete(Object, Throwable, int)} has been
		 * successfully called.  Throws a {@link CancellationException} if the task
		 * was cancelled, or a {@link ExecutionException} if the task completed with
		 * an error.
		 */
		V get() throws CancellationException, ExecutionException,
				InterruptedException {

			// Acquire the shared lock allowing interruption.
			acquireSharedInterruptibly(-1);
			return getValue();
		}

		/**
		 * Implementation of the actual value retrieval.  Will return the value
		 * on success, an exception on failure, a cancellation on cancellation, or
		 * an illegal state if the synchronizer is in an invalid state.
		 */
		private V getValue() throws CancellationException, ExecutionException {
			int state = getState();
			switch (state) {
				case COMPLETED:
					if (exception != null) {
						throw new ExecutionException(exception);
					} else {
						return value;
					}
				case CANCELLED:
				case INTERRUPTED:
					throw cancellationExceptionWithCause(
							"Task was cancelled.", exception);
				default:
					throw new IllegalStateException(
							"ERROR, synchronizer in invalid state: " + state);
			}
		}

		/**
		 * Checks if the state is {@link #COMPLETED}, {@link #CANCELLED}, or {@link #INTERRUPTED}.
		 */
		boolean isDone() {
			return (getState() & (COMPLETED | CANCELLED | INTERRUPTED)) != 0;
		}

		/**
		 * Checks if the state is {@link #CANCELLED} or {@link #INTERRUPTED}.
		 */
		boolean isCancelled() {
			return (getState() & (CANCELLED | INTERRUPTED)) != 0;
		}

		/**
		 * Checks if the state is {@link #INTERRUPTED}.
		 */
		boolean wasInterrupted() {
			return getState() == INTERRUPTED;
		}

		/**
		 * Transition to the COMPLETED state and set the value.
		 */
		boolean set(V v) {
			return complete(v, null, COMPLETED);
		}

		/**
		 * Transition to the COMPLETED state and set the exception.
		 */
		boolean setException(Throwable t) {
			return complete(null, t, COMPLETED);
		}

		/**
		 * Transition to the CANCELLED or INTERRUPTED state.
		 */
		boolean cancel(boolean interrupt) {
			return complete(null, null, interrupt ? INTERRUPTED : CANCELLED);
		}

		/**
		 * Implementation of completing a task.  Either {@code v} or {@code t} will
		 * be set but not both.  The {@code finalState} is the state to change to
		 * from {@link #RUNNING}.  If the state is not in the RUNNING state we
		 * return {@code false} after waiting for the state to be set to a valid
		 * final state ({@link #COMPLETED}, {@link #CANCELLED}, or {@link
		 * #INTERRUPTED}).
		 *
		 * @param v the value to set as the result of the computation.
		 * @param t the exception to set as the result of the computation.
		 * @param finalState the state to transition to.
		 */
		private boolean complete(V v, Throwable t, int finalState) {
			boolean doCompletion = compareAndSetState(RUNNING, COMPLETING);
			if (doCompletion) {
				// If this thread successfully transitioned to COMPLETING, set the value
				// and exception and then release to the final state.
				this.value = v;
				// Don't actually construct a CancellationException until necessary.
				this.exception = ((finalState & (CANCELLED | INTERRUPTED)) != 0)
						? new CancellationException("Future.cancel() was called.") : t;
				releaseShared(finalState);
			} else if (getState() == COMPLETING) {
				// If some other thread is currently completing the future, block until
				// they are done so we can guarantee completion.
				acquireShared(-1);
			}
			return doCompletion;
		}
	}

	static CancellationException cancellationExceptionWithCause(
			String message, Throwable cause) {
		CancellationException exception = new CancellationException(message);
		exception.initCause(cause);
		return exception;
	}

}
