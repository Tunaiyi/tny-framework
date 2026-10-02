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
package com.tny.game.net.command.dispatcher;

import com.tny.game.common.concurrent.worker.*;
import com.tny.game.net.message.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 19（Wave-C）红灯基线：每个请求恰好收到一个终答（command-execution"终答"契约）。
 * 修复前 doExecute 在 promise 完成前抛出 → finally 因 !isDone 跳过 onDone → 请求永无响应。
 */
class CommandCompletionGuardTest {

    /** 模拟真实 invoke 命令的三段协议：异常→onException 置终态→onDone 收尾 */
    private static class ProbeCommand extends RpcHandleCommand {

        final java.util.concurrent.atomic.AtomicInteger exceptionCalls = new java.util.concurrent.atomic.AtomicInteger();
        final java.util.concurrent.atomic.AtomicInteger doneCalls = new java.util.concurrent.atomic.AtomicInteger();
        volatile boolean promiseDone;
        final RuntimeException failure;

        ProbeCommand(RpcEnterContext enterContext, RuntimeException failure) {
            super(enterContext);
            this.failure = failure;
        }

        @Override
        public String getName() {
            return "probe.command";
        }

        @Override
        public boolean isDone() {
            return promiseDone;
        }

        @Override
        protected void doExecute(AsyncWorker execute) {
            throw failure;
        }

        @Override
        protected void doDone() {
            doneCalls.incrementAndGet();
        }

        @Override
        protected void onException(Throwable cause) {
            exceptionCalls.incrementAndGet();
            promiseDone = true;
        }
    }

    @Test
    @DisplayName("处理中途抛异常：必须走 onException→doDone 终结（修复前静默跳过，请求悬挂）")
    void midFlightExceptionForcesTerminalPath() {
        RpcEnterContext enterContext = org.mockito.Mockito.mock(RpcEnterContext.class);
        ProbeCommand command = new ProbeCommand(enterContext, new RuntimeException("boom before result"));

        command.execute(org.mockito.Mockito.mock(AsyncWorker.class));

        assertEquals(1, command.exceptionCalls.get(), "异常必须转入 onException（终答守卫）");
        assertEquals(1, command.doneCalls.get(), "doDone 必须收尾一次，响应以错误码发出");
    }

    @Test
    @DisplayName("兼容锚：正常完成（isDone 后）不得二次触发 onException")
    void normalCompletionUnchanged() {
        RpcEnterContext enterContext = org.mockito.Mockito.mock(RpcEnterContext.class);
        ProbeCommand command = new ProbeCommand(enterContext, null) {
            @Override
            protected void doExecute(AsyncWorker execute) {
                this.promiseDone = true;
            }
        };

        command.execute(org.mockito.Mockito.mock(AsyncWorker.class));

        assertEquals(0, command.exceptionCalls.get());
        assertEquals(1, command.doneCalls.get());
    }

    // ==================== 应答判定谓词（relay 语义表） ====================

    @Test
    @DisplayName("应答判定：普通请求恒答、中继成功移交不答、中继失败必须答、推送仅有体时答")
    void relayPredicateTruthTable() {
        assertTrue(RpcInvokeCommand.shouldRespondLocally(MessageMode.REQUEST, false, true, false));
        assertTrue(RpcInvokeCommand.shouldRespondLocally(MessageMode.REQUEST, false, false, false), "普通请求失败也要回错误码");
        assertFalse(RpcInvokeCommand.shouldRespondLocally(MessageMode.REQUEST, true, true, false), "中继成功：响应归属中继路径");
        assertTrue(RpcInvokeCommand.shouldRespondLocally(MessageMode.REQUEST, true, false, false),
                   "中继失败必须本地错误应答（修复前静默悬挂）");
        assertTrue(RpcInvokeCommand.shouldRespondLocally(MessageMode.PUSH, false, true, true));
        assertFalse(RpcInvokeCommand.shouldRespondLocally(MessageMode.PUSH, false, true, false));
        assertFalse(RpcInvokeCommand.shouldRespondLocally(MessageMode.RESPONSE, false, true, false), "响应报文不本地应答");
    }

}
