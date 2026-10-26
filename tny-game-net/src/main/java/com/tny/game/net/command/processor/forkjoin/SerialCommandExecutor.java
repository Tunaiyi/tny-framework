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
package com.tny.game.net.command.processor.forkjoin;

import com.tny.game.common.concurrent.worker.*;
import com.tny.game.common.runtime.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.*;

import static com.tny.game.net.application.NetLogger.*;
import static org.slf4j.LoggerFactory.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/3/9 02:08
 **/
public class SerialCommandExecutor implements CommandExecutor {

    private static final Logger LOG_NET = getLogger(NetLogger.EXECUTOR);

    private final AsyncWorker executeWorker;

    private final SerialAsyncWorker commandWorker;

    public SerialCommandExecutor(String name, Executor executor) {
        this.executeWorker = AsyncWorker.createSingleWorker("CommandExecutorWorker-" + name, executor);
        this.commandWorker = AsyncWorker.createSerialWorker("SerialCommandWorker-" + name, this.executeWorker);
    }

    @Override
    public void execute(@Nonnull Runnable runnable) {
        executeWorker.execute(runnable);
    }

    @Override
    public void executeCommand(RpcCommand command) {
        commandWorker.await(() -> {
            var future = execute(command);
            CompletableFuture<Object> consumed = Objects.requireNonNullElseGet(future, () -> CompletableFuture.completedFuture(null));
            // 失败可见性无条件成立（fix-executor-exception-visibility D1：
            // 原实现消费者挂在 debug 门内，生产级别下异步失败静默蒸发）
            consumed.whenComplete((value, cause) -> {
                if (cause != null) {
                    LOG_NET.error("execute [{}] command failed", command.getName(), cause);
                } else if (LOG_NET.isDebugEnabled()) {
                    LOG_NET.debug("execute [{}] command complete : {}", command.getName(), value);
                }
            });
            return consumed;
        });
    }

    @Override
    public void executeRunnable(Runnable runnable) {
        this.executeWorker.run(runnable);
    }

    private CompletableFuture<Object> execute(RpcCommand command) {
        ProcessTracer trace = NET_TRACE_INPUT_EXECUTE_COMMAND_WATCHER.trace();
        try {
            return command.execute(executeWorker);
        } catch (Throwable e) {
            LOG_NET.error("run command task {} exception", command.getName(), e);
            return null;
        } finally {
            trace.done();
        }
    }

}
