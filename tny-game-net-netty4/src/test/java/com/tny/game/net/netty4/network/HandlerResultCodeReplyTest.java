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

import com.tny.game.common.exception.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.session.MessageAllocator;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.*;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 通道异常的结果码应答（net-tunnel 规格）：携带错误级结果码的异常应先向对端定向应答。
 * 当前分类链因恒假判断为死代码，应答不发生（用例①应红）。
 */
class HandlerResultCodeReplyTest {

    NetTunnel tunnel;
    NettyMessageHandler handler;
    EmbeddedChannel channel;
    ChannelHandlerContext ctx;

    @BeforeEach
    void setUp() {
        NetworkContext context = mock(NetworkContext.class);
        when(context.getRpcMonitor()).thenReturn(new RpcMonitor());
        when(context.getSetting()).thenReturn(mock(NetBootstrapSetting.class));
        handler = new NettyMessageHandler(context);
        tunnel = mock(NetTunnel.class);
        channel = new EmbeddedChannel(handler);
        // 生产契约：send 返回非空回执且 written 可链式（NPE 教训）；stub 对齐真实返回形态
        MessageSent receipt = mock(MessageSent.class);
        @SuppressWarnings("unchecked")
        com.tny.game.common.concurrent.CompletionStageFuture<Void> stage = mock(com.tny.game.common.concurrent.CompletionStageFuture.class);
        when(receipt.written()).thenReturn(stage);
        when(tunnel.send(any(MessageContent.class))).thenReturn(receipt);
        channel.attr(NettyNetAttrKeys.TUNNEL).set(tunnel);
        ctx = channel.pipeline().context(handler);
    }

    /** 回归基线（修正后）：业务尾 handler 场景下结果码应答为既有行为，测试固化其存在 */
    @Test
    void resultCodeExceptionSendsReplyBeforePropagating() throws Exception {
        handler.exceptionCaught(ctx, new NetException(NetResultCode.AUTH_FAIL_ERROR, "fixture rejection"));

        ArgumentCaptor<MessageContent> captor = ArgumentCaptor.forClass(MessageContent.class);
        verify(tunnel, times(1)).send(captor.capture());
        assertEquals(NetResultCode.AUTH_FAIL_ERROR, captor.getValue().getResultCode(), "应答携带原结果码");
    }

    /** 普通 IO 异常不产生应答，维持传播（现状正确，绿基线） */
    @Test
    void plainIoExceptionSendsNoReply() throws Exception {
        handler.exceptionCaught(ctx, new java.io.IOException("fixture io"));

        verify(tunnel, never()).send(any(MessageContent.class));
        // EmbeddedChannel 的 tail 传播语义与真实 channel 不同（记录而不关闭），断言只锁定"无应答"
    }

}
