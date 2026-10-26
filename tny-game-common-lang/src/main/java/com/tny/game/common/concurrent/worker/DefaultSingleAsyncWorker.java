/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
                    var queuedFuture = task.getFuture();
                    if (queuedFuture != null && queuedFuture.isDone()) {
                        // 排队期间已超时：调用方脱身，不再执行副作用
                        continue;
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
