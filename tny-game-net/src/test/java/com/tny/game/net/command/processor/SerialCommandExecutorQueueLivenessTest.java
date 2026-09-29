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
package com.tny.game.net.command.processor;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.forkjoin.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 命令执行队列活性（command-execution 规格 · 场景三）：
 * 前序命令异步失败不得使后续命令停摆。（失败 error 日志断言按 design R3 降级人工验收。）
 */
class SerialCommandExecutorQueueLivenessTest {

    @Test
    void failedCommandDoesNotBlockSubsequentCommands() throws Throwable {
        ExecutorService backing = Executors.newSingleThreadExecutor(r -> new Thread(r, "liveness-test"));
        SerialCommandExecutor executor = new SerialCommandExecutor("liveness", backing);
        try {
            CountDownLatch secondExecuted = new CountDownLatch(1);

            RpcCommand failing = mock(RpcCommand.class);
            when(failing.getName()).thenReturn("failing-command");
            when(failing.execute(any())).thenReturn(
                    CompletableFuture.failedFuture(new IllegalStateException("fixture failure")));

            RpcCommand following = mock(RpcCommand.class);
            when(following.getName()).thenReturn("following-command");
            when(following.execute(any())).thenAnswer(invocation -> {
                secondExecuted.countDown();
                return CompletableFuture.completedFuture("ok");
            });

            executor.executeCommand(failing);
            executor.executeCommand(following);

            assertTrue(secondExecuted.await(5, TimeUnit.SECONDS), "后续命令必须照常执行（队列不因失败停摆）");
        } finally {
            backing.shutdownNow();
        }
    }

}
