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
import com.tny.game.common.concurrent.exception.*;
import io.netty.util.TimerTask;
import io.netty.util.*;
import org.slf4j.*;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.*;
import java.util.function.Supplier;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/9/2 20:37
 **/
public abstract class SerialWorkerExecutor implements WorkerExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerialWorkerExecutor.class);

    private static final int IDLE = 0;

    private static final int RUN = 1;

    private static final int WAITING = 2;

    public static final int CLOSE = 3;

    private static final HashedWheelTimer TIMER = new HashedWheelTimer(CoreThreadFactory.daemonWithName("SerialWorkerExecutor"));

    static {
        TIMER.start();
    }

    private final Queue<ExecuteTask<?>> taskQueue;

    private ReadWriteLock lock;

    private final AtomicInteger status = new AtomicInteger(IDLE);

    private volatile Thread currentThread;

    private final Executor masterExecutor;

    private final static ThreadLocal<WorkerExecutor> EXECUTOR_THREAD_LOCAL = new ThreadLocal<>();

    public static WorkerExecutor currentExecutor() {
        return EXECUTOR_THREAD_LOCAL.get();
    }

    public SerialWorkerExecutor(Executor masterExecutor) {
        this(masterExecutor, new ConcurrentLinkedQueue<>(), true, false);
    }

    public SerialWorkerExecutor(Executor masterExecutor, boolean immediatelyInExecutor) {
        this(masterExecutor, new ConcurrentLinkedQueue<>(), immediatelyInExecutor, false);
    }

    public SerialWorkerExecutor(Executor masterExecutor, Queue<ExecuteTask<?>> taskQueue, boolean unsafeQueue) {
        this(masterExecutor, taskQueue, true, unsafeQueue);
    }

    public SerialWorkerExecutor(Executor masterExecutor, Queue<ExecuteTask<?>> taskQueue, boolean immediatelyInExecutor, boolean unsafeQueue) {
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
    public <T> CompletableFuture<T> apply(Supplier<T> runnable, long timeout, TimeUnit unit) {
        return addTask(new AsyncApplyExecuteTask<>(runnable, timeout, unit));
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
        return addTask(new AsyncRunnableExecuteTask(runnable, timeout, unit));
    }

    @Override
    public <T> CompletableFuture<T> await(AsyncAction<T> action) {
        return await(action, 0);
    }

    @Override
    public <T> CompletableFuture<T> await(AsyncAction<T> action, long timeout) {
        return await(action, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public <T> CompletableFuture<T> await(AsyncAction<T> action, long timeout, TimeUnit unit) {
        return addTask(new AsyncActionExecuteTask<>(action, timeout, unit));
    }

    @Override
    public void execute(@Nonnull Runnable runnable) {
        addTask(new RunnableExecuteTask(runnable));
    }

    private <T> CompletableFuture<T> addTask(ExecuteTask<T> task) {
        var current = Thread.currentThread();
        if (Objects.equals(current, this.currentThread)) {
            try {
                task.execute();
                var future = task.getFuture();
                if (future != null) {
                    return task.getFuture().whenComplete((v, c) -> EXECUTOR_THREAD_LOCAL.set(this));
                }
                return null;
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
        this.writeLock();
        try {
            this.taskQueue.add(task);
            return task.getFuture();
        } finally {
            this.writeUnlock();
            this.tryLoop();
        }
    }

    //    private <T> CompletableFuture<T> addTask(AsyncExecuteTask<T> task) {
    //        var current = Thread.currentThread();
    //        if (Objects.equals(current, this.currentThread)) {
    //            try {
    //                task.execute(false);
    //                return task.getFuture().whenComplete((v, c) -> EXECUTOR_THREAD_LOCAL.set(this));
    //            } catch (Throwable e) {
    //                LOGGER.error("", e);
    //            }
    //        }
    //        this.writeLock();
    //        try {
    //            this.taskQueue.add(task);
    //            return task.getFuture();
    //        } finally {
    //            this.writeUnlock();
    //            this.tryLoop();
    //        }
    //    }

    private boolean isQueueEmpty() {
        this.readLock();
        try {
            return taskQueue.isEmpty();
        } finally {
            this.readUnlock();
        }
    }

    private void resumeLoop() {
        if (this.status.compareAndSet(WAITING, RUN)) {
            var current = EXECUTOR_THREAD_LOCAL.get();
            if (Objects.equals(this, current)) {
                this.loop();
            } else {
                masterExecutor.execute(this::loop);
            }
        }
    }

    private void loop() {
        try {
            currentThread = Thread.currentThread();
            EXECUTOR_THREAD_LOCAL.set(this);
            while (true) {
                try {
                    ExecuteTask<?> task;
                    this.writeLock();
                    try {
                        if (taskQueue.isEmpty()) {
                            break;
                        }
                        task = taskQueue.poll();
                    } finally {
                        this.writeUnlock();
                    }
                    if (task == null) {
                        break;
                    }
                    var current = task.execute();
                    if (current == null) {
                        continue;
                    }
                    status.set(WAITING);
                    return;
                } catch (Throwable e) {
                    LOGGER.error("", e);
                }
            }
        } finally {
            EXECUTOR_THREAD_LOCAL.remove();
            currentThread = null;
            if (status.compareAndSet(RUN, IDLE)) {
                tryLoop();
            }
        }
    }

    private void tryLoop() {
        if (!isQueueEmpty() && status.compareAndSet(IDLE, RUN)) {
            masterExecutor.execute(this::loop);
        }
    }

    private void writeLock() {
        if (lock != null) {
            lock.writeLock().lock();
        }
    }

    private void writeUnlock() {
        if (lock != null) {
            lock.writeLock().unlock();
        }
    }

    private void readLock() {
        if (lock != null) {
            lock.readLock().lock();
        }
    }

    private void readUnlock() {
        if (lock != null) {
            lock.readLock().unlock();
        }
    }

    private static class RunnableExecuteTask implements ExecuteTask<Void> {

        private final Runnable runnable;

        private RunnableExecuteTask(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public CompletableFuture<Void> execute() {
            runnable.run();
            return null;
        }

        @Override
        public CompletableFuture<Void> getFuture() {
            return null;
        }

    }

    private class AsyncApplyExecuteTask<T> extends AsyncExecuteTask<T> {

        private final Supplier<T> action;

        private AsyncApplyExecuteTask(Supplier<T> supplier, long timeout, TimeUnit unit) {
            super(timeout, unit);
            this.action = supplier;
        }

        @Override
        protected Object getRunner() {
            return action;
        }

        @Override
        public CompletableFuture<T> doExecute() {
            var value = this.action.get();
            return CompletableFuture.completedFuture(value);
        }

    }

    private class AsyncRunnableExecuteTask extends AsyncExecuteTask<Void> {

        private final Runnable action;

        private AsyncRunnableExecuteTask(Runnable runnable, long timeout, TimeUnit unit) {
            super(timeout, unit);
            this.action = runnable;
        }

        @Override
        protected Object getRunner() {
            return action;
        }

        @Override
        public CompletableFuture<Void> doExecute() {
            this.action.run();
            return CompletableFuture.completedFuture(null);
        }

    }

    private class AsyncActionExecuteTask<T> extends AsyncExecuteTask<T> {

        private final AsyncAction<T> action;

        private AsyncActionExecuteTask(AsyncAction<T> action, long timeout, TimeUnit unit) {
            super(timeout, unit);
            this.action = action;
        }

        @Override
        protected Object getRunner() {
            return action;
        }

        @Override
        public CompletableFuture<T> doExecute() {
            return this.action.execute();
        }

    }

    private abstract class AsyncExecuteTask<T> implements ExecuteTask<T>, TimerTask {

        private final long timeout;

        private final CompletableFuture<T> future = new CompletableFuture<>();

        protected abstract Object getRunner();

        private AsyncExecuteTask(long timeout, TimeUnit unit) {
            if (timeout > 0) {
                this.timeout = System.currentTimeMillis() + unit.toMillis(timeout);
            } else {
                this.timeout = 0;
            }
        }

        @Override
        public CompletableFuture<T> getFuture() {
            return future;
        }

        private void failed(Throwable cause) {

            future.completeExceptionally(cause);
        }

        private void handle(T value, Throwable cause) {
            if (cause != null) {
                future.completeExceptionally(cause);
            } else {
                future.complete(value);
            }
        }

        public abstract CompletableFuture<T> doExecute();

        public CompletableFuture<T> execute(boolean resumeLoop) {
            try {
                var current = doExecute();
                if (!current.isDone()) {
                    if (resumeLoop) {
                        current.whenComplete((v, c) -> {
                            handle(v, c);
                            resumeLoop();
                        });
                    } else {
                        current.whenComplete(this::handle);
                    }
                    if (timeout > 0) {
                        TIMER.newTimeout(this, System.currentTimeMillis() - this.timeout, TimeUnit.MILLISECONDS);
                    }
                    return current;
                } else {
                    current.whenComplete(this::handle);
                    return null;
                }
            } catch (Throwable e) {
                LOGGER.error("", e);
                future.completeExceptionally(e);
                throw new WorkerExecuteException(format("{} execute future is null", getRunner()), e);
            }
        }

        @Override
        public CompletableFuture<T> execute() {
            return execute(true);
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
