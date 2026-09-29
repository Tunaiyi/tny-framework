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
package com.tny.game.net.command.dispatcher;

import com.tny.game.common.concurrent.worker.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.auth.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 组 20（Wave-C）红灯基线：异步命令超时兜底（command-execution"异步命令超时兜底"契约）。
 * 修复前 3000ms 死字段无人读取——永不完成的 CompletionStage 令同会话串行队列永久停摆。
 */
class AsyncCommandTimeoutTest {

    private static final long TIMEOUT_MS = 80L;

    @Test
    @DisplayName("业务返回永不完成的异步结果：兜底时限到达后以超时码应答，promise 终结")
    void neverCompletingStageTimesOut() throws Exception {
        // 捕获 whenCompleteAsync 投递的回调，模拟串行 worker 稍后执行
        List<Runnable> pending = Collections.synchronizedList(new ArrayList<>());
        AsyncWorker worker = mock(AsyncWorker.class);
        doAnswer(inv -> {
            pending.add(inv.getArgument(0));
            return null;
        }).when(worker).execute(any(Runnable.class));

        Message message = mock(NetMessage.class);
        when(message.getMode()).thenReturn(MessageMode.REQUEST);
        when(message.getId()).thenReturn(11L);

        RpcEnterContext enterContext = mock(RpcEnterContext.class);
        when(enterContext.getMessage()).thenReturn(message);
        when(enterContext.netMessage()).thenReturn((NetMessage) message);
        NetworkContext networkContext = mock(NetworkContext.class);
        CommandExecutorFactory executorFactory = mock(CommandExecutorFactory.class);
        when(executorFactory.getCommandTimeoutMillis()).thenAnswer(i -> TIMEOUT_MS);
        when(networkContext.getCommandExecutorFactory()).thenReturn(executorFactory);
        when(enterContext.networkContext()).thenReturn(networkContext);
        NetTunnel tunnel = mock(NetTunnel.class);
        when(enterContext.netTunnel()).thenReturn(tunnel);

        MethodControllerHolder controller = mock(MethodControllerHolder.class);
        when(controller.isAuth()).thenReturn(false);
        when(controller.isActiveByAppType(any())).thenReturn(true);
        when(controller.isActiveByScope(any())).thenReturn(true);
        when(controller.isContactGroup(any())).thenReturn(true);

        NetMessageDispatcherContext dispatcherContext = mock(NetMessageDispatcherContext.class);
        when(dispatcherContext.getCommandListener()).thenReturn(List.of());
        NetAppContext appContext = mock(NetAppContext.class);
        RpcInvokeContext invokeContext = new RpcInvokeContext(controller, enterContext, appContext);

        RpcInvokeCommand command = new RpcInvokeCommand(dispatcherContext, invokeContext, mock(ContactAuthenticator.class));

        // doExecute：controller.invoke 返回永不完成的 stage
        when(controller.invoke(any(), any())).thenReturn(new CompletableFuture<>());

        command.execute(worker);

        Thread.sleep(TIMEOUT_MS * 4);
        assertFalse(invokeContext.getPromise().isDone(), "回调仍待 worker 执行（orTimeout 已终结 future）");
        pending.forEach(Runnable::run);

        assertTrue(invokeContext.getPromise().isDone(), "兜底时限后 promise 必须终结（修复前永悬挂）");
        Throwable cause = invokeContext.getPromise().getCause();
        assertNotNull(cause, "终结原因必须存在");
        Throwable root = cause;
        while (root.getCause() != null && root != root.getCause()) {
            root = root.getCause();
        }
        assertInstanceOf(TimeoutException.class, root, "根因必须为超时: " + cause);
    }

    @Test
    @DisplayName("兼容锚：时限内完成的异步结果正常应答，不触发兜底")
    void completedWithinDeadlineUnaffected() throws Exception {
        List<Runnable> pending = Collections.synchronizedList(new ArrayList<>());
        AsyncWorker worker = mock(AsyncWorker.class);
        doAnswer(inv -> {
            pending.add(inv.getArgument(0));
            return null;
        }).when(worker).execute(any(Runnable.class));

        Message message = mock(NetMessage.class);
        when(message.getMode()).thenReturn(MessageMode.REQUEST);
        when(message.getId()).thenReturn(12L);

        RpcEnterContext enterContext = mock(RpcEnterContext.class);
        when(enterContext.getMessage()).thenReturn(message);
        NetworkContext networkContext = mock(NetworkContext.class);
        CommandExecutorFactory executorFactory = mock(CommandExecutorFactory.class);
        when(executorFactory.getCommandTimeoutMillis()).thenAnswer(i -> 5000L);
        when(networkContext.getCommandExecutorFactory()).thenReturn(executorFactory);
        when(enterContext.networkContext()).thenReturn(networkContext);
        when(enterContext.netTunnel()).thenReturn(mock(NetTunnel.class));

        MethodControllerHolder controller = mock(MethodControllerHolder.class);
        when(controller.isAuth()).thenReturn(false);
        when(controller.isActiveByAppType(any())).thenReturn(true);
        when(controller.isActiveByScope(any())).thenReturn(true);
        when(controller.isContactGroup(any())).thenReturn(true);

        NetMessageDispatcherContext dispatcherContext = mock(NetMessageDispatcherContext.class);
        when(dispatcherContext.getCommandListener()).thenReturn(List.of());
        RpcInvokeContext invokeContext = new RpcInvokeContext(controller, enterContext, mock(NetAppContext.class));
        when(controller.invoke(any(), any())).thenReturn(CompletableFuture.completedFuture("ok"));

        RpcInvokeCommand command = new RpcInvokeCommand(dispatcherContext, invokeContext, mock(ContactAuthenticator.class));
        command.execute(worker);
        pending.forEach(Runnable::run);

        assertTrue(invokeContext.getPromise().isSuccess());
        assertEquals("ok", invokeContext.getPromise().getResult());
    }

}
