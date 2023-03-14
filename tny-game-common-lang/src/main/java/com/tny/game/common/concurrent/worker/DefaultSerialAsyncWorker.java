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

import com.tny.game.common.concurrent.exception.*;
import org.slf4j.*;

import javax.annotation.Nonnull;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static com.tny.game.common.utils.StringAide.*;

/**
 * 任务创新执行, 一个task完成后执行下一个任务.
 * <p>
 *
 * @author kgtny
 * @date 2022/9/2 20:37
 **/
class DefaultSerialAsyncWorker extends AbstractAsyncWorker implements SerialAsyncWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSerialAsyncWorker.class);

    public DefaultSerialAsyncWorker(String name, Executor masterExecutor) {
        super(name, masterExecutor, true);
    }

    public DefaultSerialAsyncWorker(String name, Executor masterExecutor, Queue<ExecuteTask<?>> taskQueue, boolean unsafeQueue) {
        super(name, masterExecutor, taskQueue, unsafeQueue, true);
    }

    @Override
    protected <T> CompletableFuture<T> doApply(Supplier<T> supplier, long timeout, TimeUnit unit, boolean immediateInWorker) {
        return addTask(new SerialApplyExecuteTask<>(supplier, timeout, unit), immediateInWorker);
    }

    @Override
    protected CompletableFuture<Void> doRun(Runnable runnable, long timeout, TimeUnit unit, boolean immediateInWorker) {
        return addTask(new SerialRunnableExecuteTask(runnable, timeout, unit), immediateInWorker);
    }

    @Override
    public <T> CompletableFuture<T> await(AsyncAction<T> action, long timeout, TimeUnit unit) {
        return addTask(new SerialActionExecuteTask<>(action, timeout, unit));
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
    public CompletableFuture<Void> awaitDelay(Runnable runnable, int delayTime, TimeUnit timeUnit) {
        var delay = CompletableFuture.delayedExecutor(delayTime, timeUnit);
        return await(() -> CompletableFuture.runAsync(NOOP, delay))
                .thenAccept((v) -> runnable.run());
    }

    @Override
    public <T> CompletableFuture<T> awaitDelay(Supplier<T> supplier, int delayTime, TimeUnit timeUnit) {
        var delay = CompletableFuture.delayedExecutor(delayTime, timeUnit, this);
        return await(() -> CompletableFuture.runAsync(NOOP, delay))
                .thenApply((v) -> supplier.get());
    }

    //    @Override
    //    public CompletableFuture<Void> delay(Runnable runnable, int delayTime, TimeUnit timeUnit) {
    //        boolean inWorker = isInWorker();
    //        if (inWorker) {
    //            var delay = CompletableFuture.delayedExecutor(delayTime, timeUnit, this.masterExecutor);
    //            var task = new SerialRunnableExecuteTask(runnable, 0, TimeUnit.MICROSECONDS);
    //            delay.execute(() -> runOnWait(task));
    //            return task.getFuture();
    //        } else {
    //            var delay = CompletableFuture.delayedExecutor(delayTime, timeUnit, this);
    //            return CompletableFuture.runAsync(runnable, delay);
    //        }
    //    }
    //
    //    @Override
    //    public <T> CompletableFuture<T> delay(Supplier<T> supplier, int delayTime, TimeUnit timeUnit) {
    //        boolean inWorker = isInWorker();
    //        if (inWorker) {
    //            var delay = CompletableFuture.delayedExecutor(delayTime, timeUnit, this.masterExecutor);
    //            var task = new SerialApplyExecuteTask<>(supplier, 0, TimeUnit.MICROSECONDS);
    //            delay.execute(() -> runOnWait(task));
    //            return task.getFuture();
    //        } else {
    //            var delay = CompletableFuture.delayedExecutor(delayTime, timeUnit, this);
    //            return CompletableFuture.supplyAsync(supplier, delay);
    //        }
    //    }

    @Override
    public void execute(@Nonnull Runnable runnable) {
        addTask(new SerialExecutorTask(runnable));
    }

    private <T> void runOnWait(ExecuteTask<T> task) {
        try {
            if (this.status.get() != WAITING) {
                this.addQueue(task);
            }
            currentThread = Thread.currentThread();
            EXECUTOR_THREAD_LOCAL.set(this);
            try {
                task.execute();
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        } finally {
            EXECUTOR_THREAD_LOCAL.remove();
            currentThread = null;
            if (status.compareAndSet(RUN, IDLE)) {
                tryLoop();
            }
        }
    }

    @Override
    protected <T> void postAddTask(ExecuteTask<T> task) {
        if (this.status.get() == WAITING) {
            LOGGER.debug("{} Worker 正在等待任务完成, 有可能会造成循环等待", this);
        }
        this.tryLoop();
    }

    private void resumeLoop() {
        if (this.status.compareAndSet(WAITING, RUN)) {
            masterExecutor.execute(this::loop);
        }
    }

    private void loop() {
        try {
            currentThread = Thread.currentThread();
            EXECUTOR_THREAD_LOCAL.set(this);
            while (true) {
                try {
                    ExecuteTask<?> task = pollTask();
                    if (task == null) {
                        break;
                    }
                    var current = task.execute();
                    if (current == null || current.isDone()) {
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
        if (isHasTask() && status.compareAndSet(IDLE, RUN)) {
            masterExecutor.execute(this::loop);
        }
    }

    private static class SerialExecutorTask implements ExecuteTask<Void> {

        private final Runnable runnable;

        private SerialExecutorTask(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public CompletableFuture<Void> execute() {
            try {
                runnable.run();
                return null;
            } catch (Throwable e) {
                LOGGER.error("", e);
                throw e;
            }
        }

        @Override
        public CompletableFuture<Void> getFuture() {
            return null;
        }

    }

    private class SerialRunnableExecuteTask extends SerialExecuteTask<Void> {

        private final Runnable action;

        private SerialRunnableExecuteTask(Runnable runnable, long timeout, TimeUnit unit) {
            super(timeout, unit);
            this.action = runnable;
        }

        @Override
        protected Object getRunner() {
            return action;
        }

        @Override
        public CompletableFuture<Void> doExecute() {
            try {
                this.action.run();
                return CompletableFuture.completedFuture(null);
            } catch (Throwable e) {
                LOGGER.error("", e);
                throw e;
            }
        }

    }

    private class SerialApplyExecuteTask<T> extends SerialExecuteTask<T> {

        private final Supplier<T> action;

        private SerialApplyExecuteTask(Supplier<T> supplier, long timeout, TimeUnit unit) {
            super(timeout, unit);
            this.action = supplier;
        }

        @Override
        protected Object getRunner() {
            return action;
        }

        @Override
        public CompletableFuture<T> doExecute() {
            try {
                var value = this.action.get();
                return CompletableFuture.completedFuture(value);
            } catch (Throwable e) {
                LOGGER.error("", e);
                throw e;
            }
        }

    }

    private class SerialActionExecuteTask<T> extends SerialExecuteTask<T> {

        private final AsyncAction<T> action;

        private SerialActionExecuteTask(AsyncAction<T> action, long timeout, TimeUnit unit) {
            super(timeout, unit);
            this.action = action;
        }

        @Override
        protected Object getRunner() {
            return action;
        }

        @Override
        public CompletableFuture<T> doExecute() {
            try {
                return this.action.execute();
            } catch (Throwable e) {
                LOGGER.error("", e);
                throw e;
            }
        }

    }

    private abstract class SerialExecuteTask<T> extends AsyncExecuteTask<T> {

        private SerialExecuteTask(long timeout, TimeUnit unit) {
            super(timeout, unit);
        }

        public abstract CompletableFuture<T> doExecute();

        public CompletableFuture<T> execute(boolean resumeLoop) {
            try {
                currentThread = Thread.currentThread();
                System.out.println("set currentThread " + currentThread);
                EXECUTOR_THREAD_LOCAL.set(DefaultSerialAsyncWorker.this);
                var current = doExecute();
                if (current == null) {
                    this.complete(null, null);
                    return null;
                }
                if (resumeLoop) {
                    current.whenComplete((v, c) -> {
                        EXECUTOR_THREAD_LOCAL.set(DefaultSerialAsyncWorker.this);
                        complete(v, c);
                        resumeLoop();
                    });
                } else {
                    current.whenComplete(this::complete);
                }
                return current;
            } catch (Throwable e) {
                LOGGER.error("", e);
                failed(e);
                throw new WorkerExecuteException(format("{} execute future is null", getRunner()), e);
            } finally {
                EXECUTOR_THREAD_LOCAL.remove();
                currentThread = null;
            }
        }

        @Override
        public CompletableFuture<T> execute() {
            return execute(true);
        }

    }

}
