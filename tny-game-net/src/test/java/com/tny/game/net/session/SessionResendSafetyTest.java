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
import com.tny.game.net.exception.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;import org.junit.jupiter.api.*;

import java.net.InetSocketAddress;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 会话重发缓存的登录启用链与关闭后安全（session-resend 规格）。
 */
class SessionResendSafetyTest {

    /**
     * 登录启用链（真实触发形态）：会话以缓存禁用创建，随后设置正数容量——
     * 对应 CommonSessionKeeper.doAuth 的 setSendMessageCachedSize 调用，
     * 当前实现必然 NPE（应红）。
     */
    @Test
    void loginTimeCacheEnablementMustNotBreakSession() {
        Fixture fixture = new Fixture();
        CommonSession session = new CommonSession(fixture.context, fixture.tunnel);
        assertEquals(0, session.getAllSendMessages().size(), "新会话缓存默认禁用");
        assertDoesNotThrow(() -> session.setSendMessageCachedSize(8), "登录启用缓存不得抛异常");
        assertEquals(SessionStatus.INIT, session.getStatus(), "会话状态不受调整影响");
    }

    /** 关闭后的重发三重载与查询必须安全无效（不异常、不再写出） */
    @Test
    void resendAfterCloseIsInert() {
        Fixture fixture = new Fixture();
        CommonSession session = new CommonSession(fixture.context, fixture.tunnel);
        session.close();
        int writesBefore = fixture.writes.get();

        assertDoesNotThrow(() -> session.resend(fixture.tunnel, m -> true));
        assertDoesNotThrow(() -> session.resend(fixture.tunnel, 0L, FilterBound.CLOSE));
        assertDoesNotThrow(() -> session.resend(fixture.tunnel, 0L, 10L, FilterBound.OPEN));
        assertDoesNotThrow(() -> session.getAllSendMessages());
        assertEquals(writesBefore, fixture.writes.get(), "关闭后不得产生任何写出");
    }

    /** 最小会话环境桩 */
    private static final class Fixture {
        /** 走被测真链路：BaseNetTunnel 的 close 有双检幂等守护（MockNetTunnel 测试桩无守护，见 red-baseline 发现记录） */
        final AtomicInteger writes = new AtomicInteger();
        final TestTunnel tunnel = new TestTunnel(writes);

        final SessionContext context = new SessionContext() {
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
        };
    }

    /** 最小可测隧道（close/disconnect 走 BaseNetTunnel 真实双检锁路径） */
    private static final class TestTunnel extends BaseNetTunnel<NetSession> {
        private final AtomicInteger writes;

        TestTunnel(AtomicInteger writes) {
            super(1L, NetAccessMode.SERVER, new NetBootstrapContext());
            this.writes = writes;
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
            return !isClosed();
        }

        @Override
        public MessageWriteFuture write(MessageAllocator allocator, MessageContent content) throws NetException {
            writes.incrementAndGet();
            return null;
        }

        @Override
        public MessageWriteFuture write(Message message, MessageWriteFuture promise) throws NetException {
            writes.incrementAndGet();
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

}
