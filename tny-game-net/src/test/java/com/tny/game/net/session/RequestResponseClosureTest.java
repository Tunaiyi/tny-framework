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
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 组 18（Wave-C）红灯基线：关闭×发送竞态下请求 future 必然完成（不复活 holder、不悬挂）；
 * 多播拒绝 RequestContent 共享。
 */
class RequestResponseClosureTest {

    private static MessageFactory messageFactory() {
        MessageFactory factory = mock(MessageFactory.class);
        NetMessage message = mock(NetMessage.class);
        when(message.getId()).thenReturn(777L);
        when(factory.create(anyLong(), any(MessageContent.class))).thenReturn(message);
        return factory;
    }

    /** 关闭竞态注入隧道：写入中途等待外部关闭完成后才装配消息（命中 putFuture×destroy 窗口） */
    private static final class RacingTunnel extends BaseNetTunnel<NetSession> {

        final CountDownLatch inWrite = new CountDownLatch(1);
        final CountDownLatch proceed = new CountDownLatch(1);
        final MessageFactory factory = messageFactory();

        RacingTunnel() {
            super(1L, NetAccessMode.SERVER, new NetBootstrapContext());
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
        public InetSocketAddress getRemoteAddress() {
            return new InetSocketAddress(7400);
        }

        @Override
        public InetSocketAddress getLocalAddress() {
            return new InetSocketAddress(7401);
        }

        @Override
        public MessageWriteFuture write(MessageAllocator allocator, MessageContent content) {
            inWrite.countDown();
            try {
                proceed.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            allocator.allocate(factory, content);
            MessageWriteFuture future = content.getWriteFuture();
            if (future != null) {
                future.complete(null);
            }
            return future;
        }

        @Override
        public MessageWriteFuture write(Message message, MessageWriteFuture promise) {
            return promise;
        }
    }

    private static class RacingContext implements SessionContext {

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
            return session -> new CommandExecutor() {
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
    }

    @Test
    @DisplayName("isClosed 检查与 putFuture 之间发生关闭：future 必须以失败完成而非悬挂")
    void closeDuringInflightSendCompletesFuture() throws Exception {
        RacingTunnel tunnel = new RacingTunnel();
        CommonSession session = new CommonSession(new RacingContext(), tunnel);
        Protocol proto = mock(Protocol.class);
        when(proto.getProtocolId()).thenReturn(5);
        when(proto.getLine()).thenReturn(0);

        RequestContent racing = MessageContents.request(proto, "race").willRespondFuture(5000);
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            pool.submit(() -> session.send(racing));
            assertTrue(tunnel.inWrite.await(5, TimeUnit.SECONDS), "写入应进入竞态窗口");

            session.close();
            tunnel.proceed.countDown();

            try {
                racing.getRespondFuture().get(2, TimeUnit.SECONDS);
                fail("关闭竞态下的请求 future 必须以异常终结，不得成功完成");
            } catch (ExecutionException expected) {
                // 期望路径：putFuture 命中已销毁 holder → 立即失败完成
            }
        } catch (TimeoutException timeout) {
            fail("请求 future 悬挂（修复前 holder 被复活注册，本分支即红灯）: " + timeout);
        } finally {
            pool.shutdownNow();
        }
        assertTrue(racing.getRespondFuture().isCompletedExceptionally());
    }

    @Test
    @DisplayName("多播 API 拒绝 RequestContent 共享；推送多播不受影响")
    void multicastRejectsSharedRequestContent() {
        SessionKeeperSetting setting = mock(SessionKeeperSetting.class);
        SessionSetting sessionSetting = mock(SessionSetting.class);
        when(setting.getSession()).thenReturn(sessionSetting);
        CommonSessionKeeper keeper = new CommonSessionKeeper(DefaultContactType.DEFAULT_USER, setting);

        Protocol proto = mock(Protocol.class);
        when(proto.getProtocolId()).thenReturn(6);
        assertThrows(IllegalArgumentException.class,
                     () -> keeper.sendTo(List.of(1L, 2L), MessageContents.request(proto, "shared").willRespondFuture(3000)));

        assertDoesNotThrow(() -> keeper.sendTo(List.of(1L, 2L), MessageContents.push(proto)));
        // 单目标请求仍允许
        assertDoesNotThrow(() -> keeper.sendTo(1L, MessageContents.request(proto, "single")));
    }

}
