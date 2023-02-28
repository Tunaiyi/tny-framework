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
package com.tny.game.common.concurrent.worker;

import com.tny.game.common.concurrent.*;
import io.netty.util.TimerTask;
import io.netty.util.*;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;
import java.util.function.Supplier;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/2/24 03:46
 **/
public abstract class AbstractAsyncWorker implements AsyncWorker {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractAsyncWorker.class);

    private static final HashedWheelTimer TIMER = new HashedWheelTimer(CoreThreadFactory.daemonWithName("AsyncWorkerTimer"));

    protected final static ThreadLocal<AsyncWorker> EXECUTOR_THREAD_LOCAL = new ThreadLocal<>();

    protected static final int IDLE = 0;

    protected static final int RUN = 1;

    protected static final int WAITING = 2;

    protected static final int CLOSE = 3;

    private final Queue<ExecuteTask<?>> taskQueue;

    private ReadWriteLock lock;

    protected volatile Thread currentThread;

    protected final Executor masterExecutor;

    protected final AtomicInteger status = new AtomicInteger(IDLE);

    static {
        TIMER.start();
    }
    public static AsyncWorker currentExecutor() {
        return EXECUTOR_THREAD_LOCAL.get();
    }

    public AbstractAsyncWorker(Executor masterExecutor) {
        this(masterExecutor, new ConcurrentLinkedQueue<>(), false);
    }

    public AbstractAsyncWorker(Executor masterExecutor, Queue<ExecuteTask<?>> taskQueue, boolean unsafeQueue) {
        this.masterExecutor = masterExecutor;
        this.taskQueue = taskQueue;
        if (unsafeQueue) {
            this.lock = new ReentrantReadWriteLock();
        }
    }

    @Override
    public <T> CompletableFuture<T> apply(Supplier<T> runnable) {
        return apply(runnable, 0L);
    }

    @Override
    public <T> CompletableFuture<T> apply(Supplier<T> runnable, long timeout) {
        return apply(runnable, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public CompletableFuture<Void> run(Runnable runnable) {
        return run(runnable, 0L);
    }

    @Override
    public CompletableFuture<Void> run(Runnable runnable, long timeout) {
        return run(runnable, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public CompletableFuture<Void> delay(Runnable runnable, int delayTime, TimeUnit timeUnit) {
        var delay = CompletableFuture.delayedExecutor(delayTime, timeUnit, this);
        return CompletableFuture.runAsync(runnable, delay);
    }

    @Override
    public <T> CompletableFuture<T> delay(Supplier<T> supplier, int delayTime, TimeUnit timeUnit) {
        var delay = CompletableFuture.delayedExecutor(delayTime, timeUnit, this);
        return CompletableFuture.supplyAsync(supplier, delay);
    }

    protected boolean isInWorker() {
        return Objects.equals(Thread.currentThread(), this.currentThread);
    }

    protected <T> CompletableFuture<T> addTask(ExecuteTask<T> task) {
        if (isInWorker()) {
            try {
                task.execute();
                var future = task.getFuture();
                if (future != null) {
                    return task.getFuture().whenComplete((v, c) -> EXECUTOR_THREAD_LOCAL.set(this));
                }
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
        return this.addQueue(task);
    }

    protected <T> CompletableFuture<T> addQueue(ExecuteTask<T> task) {
        this.writeLock();
        try {
            this.taskQueue.add(task);
            return task.getFuture();
        } finally {
            this.writeUnlock();
            postAddTask(task);
        }
    }

    protected ExecuteTask<?> pollTask() {
        this.writeLock();
        try {
            return taskQueue.poll();
        } finally {
            this.writeUnlock();
        }
    }

    protected abstract <T> void postAddTask(ExecuteTask<T> task);

    protected boolean isHasTask() {
        this.readLock();
        try {
            return !taskQueue.isEmpty();
        } finally {
            this.readUnlock();
        }
    }

    protected void writeLock() {
        if (lock != null) {
            lock.writeLock().lock();
        }
    }

    protected void writeUnlock() {
        if (lock != null) {
            lock.writeLock().unlock();
        }
    }

    protected void readLock() {
        if (lock != null) {
            lock.readLock().lock();
        }
    }

    protected void readUnlock() {
        if (lock != null) {
            lock.readLock().unlock();
        }
    }

    protected abstract static class AsyncExecuteTask<T> implements ExecuteTask<T>, TimerTask {

        private final CompletableFuture<T> future = new CompletableFuture<>();

        private volatile Timeout timeout;

        protected AsyncExecuteTask(long timeout, TimeUnit unit) {
            if (timeout > 0) {
                this.timeout = this.startTimeout(unit.toMillis(timeout));
            } else {
                this.timeout = null;
            }
        }

        protected void tryTimeout(long timeout, TimeUnit unit) {
            if (this.timeout == null && timeout > 0) {
                this.timeout = this.startTimeout(unit.toMillis(timeout));
            }
        }

        protected abstract Object getRunner();

        @Override
        public CompletableFuture<T> getFuture() {
            return future;
        }

        protected Timeout startTimeout(long timeout) {
            if (timeout > 0) {
                return TIMER.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
            }
            return null;
        }

        protected void failed(Throwable cause) {
            if (this.timeout != null) {
                this.timeout.cancel();
            }
            future.completeExceptionally(cause);
        }

        protected void complete(T value, Throwable cause) {
            if (this.timeout != null) {
                this.timeout.cancel();
            }
            if (cause != null) {
                future.completeExceptionally(cause);
            } else {
                future.complete(value);
            }
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            if (future.isDone()) {
                return;
            }
            future.completeExceptionally(new TimeoutException());
        }

    }

}
