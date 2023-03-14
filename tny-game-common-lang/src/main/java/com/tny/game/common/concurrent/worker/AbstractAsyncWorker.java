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

    private final String name;

    private final Queue<ExecuteTask<?>> taskQueue;

    private ReadWriteLock lock;

    private final boolean executeImmediatelyInWorker;

    protected volatile Thread currentThread;

    protected final Executor masterExecutor;

    protected final AtomicInteger status = new AtomicInteger(IDLE);

    static {
        TIMER.start();
    }

    @Override
    public String getName() {
        return name;
    }

    public static AsyncWorker current() {
        return EXECUTOR_THREAD_LOCAL.get();
    }

    public AbstractAsyncWorker(String name, Executor masterExecutor, boolean executeImmediatelyInWorker) {
        this(name, masterExecutor, new ConcurrentLinkedQueue<>(), executeImmediatelyInWorker, false);
    }

    public AbstractAsyncWorker(String name, Executor masterExecutor, Queue<ExecuteTask<?>> taskQueue, boolean unsafeQueue,
            boolean executeImmediatelyInWorker) {
        this.name = name;
        this.masterExecutor = masterExecutor;
        this.taskQueue = taskQueue;
        this.executeImmediatelyInWorker = executeImmediatelyInWorker;
        if (unsafeQueue) {
            this.lock = new ReentrantReadWriteLock();
        }
    }

    @Override
    public <T> CompletableFuture<T> apply(Supplier<T> supplier) {
        return apply(supplier, 0L);
    }

    @Override
    public <T> CompletableFuture<T> apply(Supplier<T> supplier, long timeout) {
        return apply(supplier, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public <T> CompletableFuture<T> apply(Supplier<T> supplier, long timeout, TimeUnit unit) {
        return doApply(supplier, timeout, unit, executeImmediatelyInWorker);
    }

    @Override
    public <T> CompletableFuture<T> applyNext(Supplier<T> supplier) {
        return applyNext(supplier, 0L);
    }

    @Override
    public <T> CompletableFuture<T> applyNext(Supplier<T> supplier, long timeout) {
        return applyNext(supplier, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public <T> CompletableFuture<T> applyNext(Supplier<T> supplier, long timeout, TimeUnit unit) {
        return doApply(supplier, timeout, unit, false);
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
    public CompletableFuture<Void> run(Runnable runnable, long timeout, TimeUnit unit) {
        return doRun(runnable, timeout, unit, executeImmediatelyInWorker);
    }

    @Override
    public CompletableFuture<Void> runNext(Runnable runnable) {
        return runNext(runnable, 0L);
    }

    @Override
    public CompletableFuture<Void> runNext(Runnable runnable, long timeout) {
        return runNext(runnable, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public CompletableFuture<Void> runNext(Runnable runnable, long timeout, TimeUnit unit) {
        return doRun(runnable, timeout, unit, false);
    }

    protected abstract CompletableFuture<Void> doRun(Runnable runnable, long timeout, TimeUnit unit, boolean immediateInWorker);

    protected abstract <T> CompletableFuture<T> doApply(Supplier<T> supplier, long timeout, TimeUnit unit, boolean immediateInWorker);

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
        return addTask(task, executeImmediatelyInWorker);
    }

    protected <T> CompletableFuture<T> addTask(ExecuteTask<T> task, boolean executeImmediatelyInWorker) {
        if (executeImmediatelyInWorker && isInWorker()) {
            try {
                task.execute();
                var future = task.getFuture();
                if (future != null) {
                    return future.whenComplete((v, c) -> EXECUTOR_THREAD_LOCAL.set(this));
                }
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
            return null;
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

    @Override
    public String toString() {
        return new StringJoiner(", ", AbstractAsyncWorker.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .toString();
    }

    protected abstract class AsyncExecuteTask<T> implements ExecuteTask<T>, TimerTask {

        private final CompletableFuture<T> future = new CompletableFuture<>();

        private final Timeout timeout;

        protected AsyncExecuteTask(long timeout, TimeUnit unit) {
            this.timeout = this.startTimeout(unit.toMillis(timeout));
        }

        protected Timeout startTimeout(long timeout) {
            if (timeout > 0) {
                return TIMER.newTimeout(this, timeout, TimeUnit.MILLISECONDS);
            }
            return null;
        }

        protected abstract Object getRunner();

        @Override
        public CompletableFuture<T> getFuture() {
            return future;
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
                var worker = AbstractAsyncWorker.this;
                LOGGER.error("execute exception on {} {} ", worker.getClass().getSimpleName(), worker.getName(), cause);
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
