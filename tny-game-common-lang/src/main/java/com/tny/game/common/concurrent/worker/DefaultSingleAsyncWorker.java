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
 * 单线程执行 task, 无需等待 task 完成
 * <p>
 *
 * @author kgtny
 * @date 2022/9/2 20:37
 **/
class DefaultSingleAsyncWorker extends AbstractAsyncWorker {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSingleAsyncWorker.class);

    public DefaultSingleAsyncWorker(String name, Executor masterExecutor) {
        super(name, masterExecutor, false);
    }

    public DefaultSingleAsyncWorker(String name, Executor masterExecutor, Queue<ExecuteTask<?>> taskQueue, boolean unsafeQueue) {
        super(name, masterExecutor, taskQueue, unsafeQueue, false);
    }

    @Override
    protected CompletableFuture<Void> doRun(Runnable runnable, long timeout, TimeUnit unit, boolean immediateInWorker) {
        return addTask(new SingleRunnableExecuteTask(runnable, timeout, unit), immediateInWorker);
    }

    @Override
    protected <T> CompletableFuture<T> doApply(Supplier<T> supplier, long timeout, TimeUnit unit, boolean immediateInWorker) {
        return addTask(new SingleApplyExecuteTask<>(supplier, timeout, unit), immediateInWorker);
    }

    @Override
    public void execute(@Nonnull Runnable runnable) {
        addTask(new SingleExecutorTask(runnable));
    }

    @Override
    protected <T> void postAddTask(ExecuteTask<T> task) {
        this.tryLoop();
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
                    task.execute();
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

    private static class SingleExecutorTask implements ExecuteTask<Void> {

        private final Runnable runnable;

        private SingleExecutorTask(Runnable runnable) {
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

    private class SingleApplyExecuteTask<T> extends SingleExecuteTask<T> {

        private final Supplier<T> action;

        private SingleApplyExecuteTask(Supplier<T> supplier, long timeout, TimeUnit unit) {
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

    private class SingleRunnableExecuteTask extends SingleExecuteTask<Void> {

        private final Runnable action;

        private SingleRunnableExecuteTask(Runnable runnable, long timeout, TimeUnit unit) {
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

    private class SingleActionExecuteTask<T> extends SingleExecuteTask<T> {

        private final AsyncAction<T> action;

        private SingleActionExecuteTask(AsyncAction<T> action, long timeout, TimeUnit unit) {
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

    private abstract class SingleExecuteTask<T> extends AsyncExecuteTask<T> {

        protected SingleExecuteTask(long timeout, TimeUnit unit) {
            super(timeout, unit);
        }

        public abstract CompletableFuture<T> doExecute();

        @Override
        public CompletableFuture<T> execute() {
            try {
                var current = doExecute();
                if (current != null) {
                    current.whenComplete(this::complete);
                } else {
                    this.complete(null, null);
                }
                return null;
            } catch (Throwable e) {
                LOGGER.error("", e);
                failed(e);
                throw new WorkerExecuteException(format("{} execute future is null", getRunner()), e);
            }
        }

    }

}
