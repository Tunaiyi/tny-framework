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
