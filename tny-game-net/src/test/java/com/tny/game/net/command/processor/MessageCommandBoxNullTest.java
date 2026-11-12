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
package com.tny.game.net.command.processor;

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 9（Wave-A）红灯基线：未知模式报文安全吸收（command-execution"未知模式"契约）。
 * 修复前 PONG 走 default 分支返回 null 命令仍入执行器 → 三层 NPE 异常刷屏。
 */
class MessageCommandBoxNullTest {

    @Test
    @DisplayName("PONG 报文：不入执行队列、上下文完成、无异常")
    void pongIsSafelyAbsorbed() {
        CommandExecutor executor = mock(CommandExecutor.class);
        MessageCommandBox box = new MessageCommandBox(executor);
        RpcEnterContext context = mock(RpcEnterContext.class);
        when(context.getMessage()).thenReturn(TickMessage.pong());

        assertDoesNotThrow(() -> box.addCommand(context));
        verifyNoInteractions(executor);
        verify(context).complete();
    }

    @Test
    @DisplayName("兼容锚：PING 报文照常产生 pong 命令并入队")
    void pingStillEnqueuesPongCommand() {
        CommandExecutor executor = mock(CommandExecutor.class);
        MessageCommandBox box = new MessageCommandBox(executor);
        RpcEnterContext context = mock(RpcEnterContext.class);
        NetTunnel tunnel = mock(NetTunnel.class);
        when(context.getMessage()).thenReturn(TickMessage.ping());
        when(context.netTunnel()).thenReturn(tunnel);

        assertTrue(box.addCommand(context));
        verify(executor).executeCommand(any(RpcCommand.class));
    }

}
