/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.worker;

import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * A cancellable asynchronous computation. This class provides a base
 * implementation of {@link Future}, with methods to start and cancel a
 * computation, query to see if the computation is complete, and retrieve the
 * result of the computation. The result can only be retrieved when the
 * computation has completed; the <tt>get</tt> method will block if the
 * computation has not yet completed. Once the computation has completed, the
 * computation cannot be restarted or cancelled.
 * <p>
 * <p>
 * A <tt>FutureTask</tt> can be used to wrap a {@link Callable} or
 * {@link Runnable} object. Because <tt>FutureTask</tt> implements
 * <tt>Runnable</tt>, a <tt>FutureTask</tt> can be submitted to an
 * {@link Executor} for execution.
 * <p>
 * <p>
 * In addition to serving as a standalone class, this class provides
 * <tt>protected</tt> functionality that may be useful when creating customized
 * task classes.
 *
 * @param <V> The result type returned by this FutureTask's <tt>get</tt> method
 * @author Doug Lea
 * @since 1.5
 */
public class FutureTask<V> implements RunnableFuture<V> {

    /**
     * Synchronization control for FutureTask
     */
    private final Sync sync;

    /**
     * Creates a <tt>FutureTask</tt> that will upon running, execute the given
     * <tt>Callable</tt>.
     *
     * @param callable the callable task
     * @throws NullPointerException if callable is null
     */
    public FutureTask(Callable<V> callable) {
        if (callable == null) {
            throw new NullPointerException();
        }
        this.sync = new Sync(callable);
    }

    /**
     * Creates a <tt>FutureTask</tt> that will upon running, execute the given
     * <tt>Runnable</tt>, and arrange that <tt>get</tt> will return the given
     * result on successful completion.
     *
     * @param runnable the runnable task
     * @param result   the result to return on successful completion. If you don't
     *                 need a particular result, consider using constructions of the
     *                 form:
     *                 <tt>Future&lt;?&gt; f = new FutureTask&lt;Object&gt;(runnable, null)</tt>
     * @throws NullPointerException if runnable is null
     */
    public FutureTask(Runnable runnable, V result) {
        this.sync = new Sync(Executors.callable(runnable, result));
    }

    @Override
    public boolean isCancelled() {
        return this.sync.innerIsCancelled();
    }

    @Override
    public boolean isDone() {
        return this.sync.innerIsDone();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return this.sync.innerCancel(mayInterruptIfRunning);
    }

    /**
     * @throws CancellationException {@inheritDoc}
     */
    @Override
    public V get() throws InterruptedException, ExecutionException {
        return this.sync.innerGet();
    }

    /**
     * @throws CancellationException {@inheritDoc}
     */
    @Override
    public V get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return this.sync.innerGet(unit.toNanos(timeout));
    }

    /**
     * Protected method invoked when this task transitions to state
     * <tt>isDone</tt> (whether normally or via cancellation). The default
     * implementation does nothing. Subclasses may override this method to
     * invoke completion callbacks or perform bookkeeping. Note that you can
     * query status inside the implementation of this method to determine
     * whether this task has been cancelled.
     */
    protected void done() {
    }

    /**
     * Sets the result of this Future to the given value unless this future has
     * already been set or has been cancelled. This method is invoked internally
     * by the <tt>run</tt> method upon successful completion of the computation.
     *
     * @param v the value
     */
    protected void set(V v) {
        this.sync.innerSet(v);
    }

    /**
     * Causes this future to report an <tt>ExecutionException</tt> with the
     * given throwable as its cause, unless this Future has already been set or
     * has been cancelled. This method is invoked internally by the <tt>run</tt>
     * method upon failure of the computation.
     *
     * @param t the cause of failure
     */
    protected void setException(Throwable t) {
        this.sync.innerSetException(t);
    }

    // The following (duplicated) doc comment can be removed once
    //
    // 6270645: Javadoc comments should be inherited from most derived
    //          superinterface or superclass
    // is fixed.

    /**
     * Sets this Future to the result of its computation unless it has been
     * cancelled.
     */
    @Override
    public void run() {
        this.sync.innerRun();
    }

    /**
     * Executes the computation without setting its result, and then resets this
     * Future to initial state, failing to do so if the computation encounters
     * an exception or is cancelled. This is designed for use with tasks that
     * intrinsically execute more than once.
     *
     * @return true if successfully run and reset
     */
    protected boolean runAndReset() {
        return this.sync.innerRunAndReset();
    }

    protected boolean reset() {
        return this.sync.reset();
    }

    /**
     * Synchronization control for FutureTask. Note that this must be a
     * non-static inner class in order to invoke the protected <tt>done</tt>
     * method. For clarity, all inner class support methods are same as outer,
     * prefixed with "inner".
     * <p>
     * Uses AQS sync state to represent run status
     */
    private final class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = -7828117401763700385L;

        /**
         * State value representing that task is running
         */
        private static final int RUNNING = 1;

        /**
         * State value representing that task ran
         */
        private static final int RAN = 2;

        /**
         * State value representing that task was cancelled
         */
        private static final int CANCELLED = 4;

        /**
         * The underlying callable
         */
        private final Callable<V> callable;

        /**
         * The result to return from get()
         */
        private V result;

        /**
         * The exception to throw from get()
         */
        private Throwable exception;

        /**
         * The thread running task. When nulled after set/cancel, this indicates
         * that the results are accessible. Must be volatile, to ensure
         * visibility upon completion.
         */
        private volatile Thread runner;

        Sync(Callable<V> callable) {
            this.callable = callable;
        }

        private boolean ranOrCancelled(int state) {
            return (state & (RAN | CANCELLED)) != 0;
        }

        /**
         * Implements AQS base acquire to succeed if ran or cancelled
         */
        @Override
        protected int tryAcquireShared(int ignore) {
            return innerIsDone() ? 1 : -1;
        }

        /**
         * Implements AQS base release to always signal after setting final done
         * status by nulling runner thread.
         */
        @Override
        protected boolean tryReleaseShared(int ignore) {
            this.runner = null;
            return true;
        }

        boolean innerIsCancelled() {
            return getState() == CANCELLED;
        }

        boolean innerIsDone() {
            return ranOrCancelled(getState()) && this.runner == null;
        }

        V innerGet() throws InterruptedException, ExecutionException {
            acquireSharedInterruptibly(0);
            if (getState() == CANCELLED) {
                throw new CancellationException();
            }
            if (this.exception != null) {
                throw new ExecutionException(this.exception);
            }
            return this.result;
        }

        V innerGet(long nanosTimeout) throws InterruptedException, ExecutionException, TimeoutException {
            if (!tryAcquireSharedNanos(0, nanosTimeout)) {
                throw new TimeoutException();
            }
            if (getState() == CANCELLED) {
                throw new CancellationException();
            }
            if (this.exception != null) {
                throw new ExecutionException(this.exception);
            }
            return this.result;
        }

        void innerSet(V v) {
            for (; ; ) {
                int s = getState();
                if (s == RAN) {
                    return;
                }
                if (s == CANCELLED) {
                    // aggressively release to set runner to null,
                    // in case we are racing with a cancel request
                    // that will try to interrupt runner
                    releaseShared(0);
                    return;
                }
                if (compareAndSetState(s, RAN)) {
                    this.result = v;
                    releaseShared(0);
                    done();
                    return;
                }
            }
        }

        void innerSetException(Throwable t) {
            for (; ; ) {
                int s = getState();
                if (s == RAN) {
                    return;
                }
                if (s == CANCELLED) {
                    // aggressively release to set runner to null,
                    // in case we are racing with a cancel request
                    // that will try to interrupt runner
                    releaseShared(0);
                    return;
                }
                if (compareAndSetState(s, RAN)) {
                    this.exception = t;
                    this.result = null;
                    releaseShared(0);
                    done();
                    return;
                }
            }
        }

        boolean innerCancel(boolean mayInterruptIfRunning) {
            for (; ; ) {
                int s = getState();
                if (ranOrCancelled(s)) {
                    return false;
                }
                if (compareAndSetState(s, CANCELLED)) {
                    break;
                }
            }
            if (mayInterruptIfRunning) {
                Thread r = this.runner;
                if (r != null) {
                    r.interrupt();
                }
            }
            releaseShared(0);
            done();
            return true;
        }

        void innerRun() {
            if (!compareAndSetState(0, RUNNING)) {
                return;
            }
            try {
                this.runner = Thread.currentThread();
                if (getState() == RUNNING) // recheck after setting thread
                {
                    innerSet(this.callable.call());
                } else {
                    releaseShared(0); // cancel
                }
            } catch (Throwable ex) {
                innerSetException(ex);
            }
        }

        boolean reset() {
            return compareAndSetState(getState(), 0);
        }

        boolean innerRunAndReset() {
            if (!compareAndSetState(0, RUNNING)) {
                return false;
            }
            try {
                this.runner = Thread.currentThread();
                if (getState() == RUNNING) {
                    this.callable.call(); // don't set result
                }
                this.runner = null;
                return compareAndSetState(RUNNING, 0);
            } catch (Throwable ex) {
                innerSetException(ex);
                return false;
            }
        }

    }

}