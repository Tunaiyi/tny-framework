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
            if (LOG_NET.isDebugEnabled()) {
                LOG_NET.debug("execute [{}] command | wait {}", command.getName(), future != null);
                if (future != null) {
                    future.whenComplete((value, cause) -> {
                        if (cause != null) {
                            LOG_NET.error("execute [{}] command failed", command.getName(), cause);
                        } else {
                            LOG_NET.debug("execute [{}] command complete : {}", command.getName(), value);
                        }
                    });
                }
            }
            return Objects.requireNonNullElseGet(future, () -> CompletableFuture.completedFuture(null));
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
