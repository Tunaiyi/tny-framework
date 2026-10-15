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
