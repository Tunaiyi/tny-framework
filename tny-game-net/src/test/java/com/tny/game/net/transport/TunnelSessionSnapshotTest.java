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
package com.tny.game.net.transport;

import com.tny.game.common.result.*;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.message.common.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;
import org.junit.jupiter.api.*;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 通道消息以单一会话快照处理的回归测试（net-tunnel 规格）。
 * <p>
 * 对应 Scenario「重连切换期间进入的消息归属旧会话」与「无并发切换时的常规接收」：
 * 消息处理开始时读取的会话引用，在整个处理过程中不得被并发的会话切换撕裂。
 */
class TunnelSessionSnapshotTest {

    /**
     * 会话切换与消息接收并发时，先到的消息完整落入旧会话，后到的消息落入新会话。
     */
    @Test
    void inFlightMessageBelongsToSnapshotSessionAcrossSwap() throws Exception {
        TestTunnel tunnel = new TestTunnel();
        BlockingSession oldSession = new BlockingSession();
        RecordingSession newSession = new RecordingSession();

        tunnel.resetSession(oldSession);

        NetMessage first = message(1L, "first");
        CountDownLatch processingStarted = new CountDownLatch(1);
        CountDownLatch allowFinish = new CountDownLatch(1);
        oldSession.hook = () -> {
            processingStarted.countDown();
            try {
                allowFinish.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread receiver = new Thread(() -> tunnel.receive(first), "snapshot-receiver");
        receiver.start();

        assertTrue(processingStarted.await(5, TimeUnit.SECONDS), "旧会话应已开始处理第一条消息");
        // 模拟重连切换：与 ServerTransportTunnel.resetSession 同形态（单次引用赋值）
        tunnel.resetSession(newSession);
        allowFinish.countDown();
        receiver.join(5_000);
        assertFalse(receiver.isAlive(), "接收线程应已完成");

        assertEquals(1, oldSession.handled.size(), "已进入处理的消息必须归属旧会话");
        assertSame(first, oldSession.handled.get(0));
        assertEquals(0, newSession.handled.size(), "切换期间旧消息不得混入新会话");

        // 后续消息按新会话快照处理
        NetMessage second = message(2L, "second");
        assertTrue(tunnel.receive(second));
        assertEquals(1, newSession.handled.size());
        assertSame(second, newSession.handled.get(0));
        assertEquals(1, oldSession.handled.size(), "旧会话不得再收到新消息");
    }

    private static NetMessage message(long id, String body) {
        MessageContent content = MessageContents.push(Protocols.protocol(1000), ResultCode.SUCCESS, body);
        return new CommonMessageFactory().create(id, content);
    }

    /**
     * 最小可测通道：继承被测类 BaseNetTunnel，会话切换实现与 ServerTransportTunnel 一致
     * （单次 volatile 引用赋值）。
     */
    private static final class TestTunnel extends BaseNetTunnel<NetSession> {
        private int opened;

        TestTunnel() {
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
            opened++;
        }

        int openedCount() {
            return opened;
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
            return !isClosed();
        }

        @Override
        public MessageWriteFuture write(MessageAllocator allocator, MessageContent content) throws NetException {
            // 本测试只驱动接收路径，发送保持 no-op
            return null;
        }

        @Override
        public MessageWriteFuture write(Message message, MessageWriteFuture promise) throws NetException {
            // 本测试只驱动接收路径，发送保持 no-op
            return promise;
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return new InetSocketAddress(7100);
        }

        @Override
        public InetSocketAddress getLocalAddress() {
            return new InetSocketAddress(7000);
        }
    }

    /** 处理中可阻塞的会话，用于制造“处理进行中 × 会话切换”的并发窗口 */
    private static final class BlockingSession extends MockNetSession {
        final List<Message> handled = Collections.synchronizedList(new ArrayList<>());
        volatile Runnable hook;

        BlockingSession() {
            super(Certificates.anonymous(), NetAccessMode.SERVER);
        }

        @Override
        public boolean receive(RpcEnterContext context) {
            Runnable current = hook;
            if (current != null) {
                current.run();
            }
            handled.add(context.getMessage());
            return true;
        }
    }

    /** 即时完成的记录会话 */
    private static final class RecordingSession extends MockNetSession {
        final List<Message> handled = Collections.synchronizedList(new ArrayList<>());

        RecordingSession() {
            super(Certificates.anonymous(), NetAccessMode.SERVER);
        }

        @Override
        public boolean receive(RpcEnterContext context) {
            handled.add(context.getMessage());
            return true;
        }
    }

}
