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
package com.tny.game.net.session;

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.command.processor.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import java.net.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 组 18 附带面：重发路径同受发送过滤器约束（过滤器语义不得在重发链失效）。
 */
class ResendFilterConsistencyTest {

    private static final class CapturingTunnel extends BaseNetTunnel<NetSession> {

        final AtomicInteger directWrites = new AtomicInteger();
        final MessageFactory factory = messageFactory();

        CapturingTunnel() {
            super(1L, NetAccessMode.SERVER, new NetBootstrapContext());
        }

        static MessageFactory messageFactory() {
            MessageFactory factory = mock(MessageFactory.class);
            NetMessage message = mock(NetMessage.class);
            when(message.getId()).thenReturn(7L);
            when(factory.create(anyLong(), any(MessageContent.class))).thenReturn(message);
            return factory;
        }

        @Override
        protected boolean resetSession(NetSession newSession) {
            this.session = newSession;
            return true;
        }

        @Override
        protected boolean onOpen() {
            return true;
        }

        @Override
        protected void onOpened() {
        }

        @Override
        protected void onClose() {
        }

        @Override
        protected void onClosed() {
        }

        @Override
        protected void onDisconnected() {
        }

        @Override
        protected void doDisconnect() {
        }

        @Override
        public boolean isActive() {
            return getStatus() == TunnelStatus.OPEN;
        }

        @Override
        public MessageWriteFuture write(MessageAllocator allocator, MessageContent content) {
            // 真实装配消息进已发送缓存（与生产写链一致）
            allocator.allocate(factory, content);
            MessageWriteFuture future = content.getWriteFuture();
            if (future != null) {
                future.complete(null);
            }
            return future;
        }

        @Override
        public MessageWriteFuture write(Message message, MessageWriteFuture promise) {
            directWrites.incrementAndGet();
            return promise;
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return new InetSocketAddress(7500);
        }

        @Override
        public InetSocketAddress getLocalAddress() {
            return new InetSocketAddress(7501);
        }
    }

    @Test
    @DisplayName("重发经发送过滤器：过滤拒绝的消息不得写出（修复前直写绕过过滤器）")
    void resendHonorsSendFilter() {
        CapturingTunnel tunnel = new CapturingTunnel();
        CommonSession session = new CommonSession(new SessionContext() {
            @Override
            public NetAccessMode getAccessMode() {
                return NetAccessMode.SERVER;
            }

            @Override
            public MessageDispatcher getMessageDispatcher() {
                return null;
            }

            @Override
            public CommandExecutorFactory getCommandExecutorFactory() {
                return s -> new CommandExecutor() {
                    @Override
                    public void executeCommand(RpcCommand command) {
                    }

                    @Override
                    public void executeRunnable(Runnable runnable) {
                    }

                    @Override
                    public void execute(Runnable command) {
                    }
                };
            }
        }, tunnel);
        assertTrue(tunnel.open());
        session.setSendMessageCachedSize(8);

        Protocol proto = mock(Protocol.class);
        when(proto.getProtocolId()).thenReturn(3);
        when(proto.getLine()).thenReturn(0);
        session.send(MessageContents.request(proto, "x"));

        // 前置：无过滤时重发产生一次写出
        session.resend(null, message -> true);
        assertEquals(1, tunnel.directWrites.get(), "前置：缓存命中应重发一次");

        session.setSendFilter((target, subject) -> MessageHandleStrategy.IGNORE);
        session.resend(null, message -> true);
        assertEquals(1, tunnel.directWrites.get(),
                "过滤器全忽略后重发不得写出（修复前 resend 绕过 sendFilter，本断言应红）");
    }

}
