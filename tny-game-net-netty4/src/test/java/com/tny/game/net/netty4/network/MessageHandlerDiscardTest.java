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
package com.tny.game.net.netty4.network;

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 会话未就绪丢弃的行为面（net-tunnel 规格）：丢弃安全、无副作用、不崩溃。
 * 日志留痕断言按 design R3 降级为人工验收（slf4j-simple 无内存 Appender，见 red-baseline）。
 */
class MessageHandlerDiscardTest {

    /** TUNNEL 未就绪时收到消息：安全丢弃，不抛异常不产生处理副作用（当前 NPE 被吞亦不抛，绿基线） */
    @Test
    void messageWithoutReadyTunnelIsSafelyDiscarded() throws Exception {
        NetworkContext context = mock(NetworkContext.class);
        when(context.getRpcMonitor()).thenReturn(new RpcMonitor());
        when(context.getSetting()).thenReturn(mock(NetBootstrapSetting.class));
        NettyMessageHandler handler = new NettyMessageHandler(context);

        io.netty.channel.embedded.EmbeddedChannel channel = new io.netty.channel.embedded.EmbeddedChannel(handler);
        // 不设置 TUNNEL attr：制造绑定失败窗口
        Message message = mock(Message.class);

        assertDoesNotThrow(() -> handler.channelRead(channel.pipeline().firstContext(), message));
        // 未产生任何会话级处理副作用：message 仅被读取判定，无 receive 交互
        verify(message, never()).getHead();
    }

}
