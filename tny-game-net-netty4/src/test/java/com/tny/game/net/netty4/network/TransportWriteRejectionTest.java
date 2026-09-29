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

import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;
import io.netty.channel.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.RejectedExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 18（Wave-C）红灯基线：写提交被 event-loop 拒绝时，
 * 写回执与响应等待必须以失败终结，不得永久悬挂（net-tunnel"写提交失败必须完成回执"契约）。
 */
class TransportWriteRejectionTest {

    @Test
    @DisplayName("event-loop 拒绝提交：awaiter 失败完成、响应等待被取消")
    void rejectedSubmissionCompletesReceipts() throws Exception {
        Channel channel = mock(Channel.class);
        EventLoop eventLoop = mock(EventLoop.class);
        doThrow(new RejectedExecutionException("stub loop shutdown")).when(eventLoop).execute(any(Runnable.class));
        when(channel.eventLoop()).thenReturn(eventLoop);
        NettyChannelMessageTransport transport = new NettyChannelMessageTransport(NetAccessMode.CLIENT, channel);

        Protocol proto = mock(Protocol.class);
        when(proto.getProtocolId()).thenReturn(9);
        when(proto.getLine()).thenReturn(0);
        RequestContent content = MessageContents.request(proto, "p");
        content.willRespondFuture(5000L);
        content.willWriteFuture();
        MessageWriteFuture awaiter = content.getWriteFuture();
        MessageRespondFuture respond = content.getRespondFuture();

        transport.write((factory, ctx) -> mock(NetMessage.class), mock(MessageFactory.class), content);

        assertTrue(awaiter.isCompletedExceptionally(), "写回执必须失败完成（修复前悬挂）");
        assertTrue(respond.isCancelled() || respond.isCompletedExceptionally(),
                "响应等待不得保持可悬挂状态");
    }

}
