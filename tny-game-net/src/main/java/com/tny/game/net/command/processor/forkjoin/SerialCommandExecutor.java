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
import com.tny.game.net.base.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.MessageCommandBox;
import com.tny.game.net.command.processor.*;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.concurrent.*;

import static com.tny.game.net.base.NetLogger.*;
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

    public SerialCommandExecutor(Executor executor) {
        this.executeWorker = AsyncWorker.createSingleWorker(executor);
        this.commandWorker = AsyncWorker.createSerialWorker(executor);
    }

    @Override
    public void execute(Runnable runnable) {
        executeWorker.execute(runnable);
    }

    @Override
    public void executeCommand(MessageCommandBox box, RpcCommand command) {
        commandWorker.await(() -> {
            var future = execute(command);
            return Objects.requireNonNullElseGet(future, () -> CompletableFuture.completedFuture(null));
        });
    }

    @Override
    public void executeRunnable(MessageCommandBox box, Runnable runnable) {
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