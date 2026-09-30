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

import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 6（Wave-A）红灯基线：下线转换幂等且事件单次（net-session"下线转换幂等"契约）。
 * 修复前 setOffline 无状态短路：offline 回调内 close() 递归至 StackOverflow；
 * 通道断开与会话关闭并/串发时离线通知两次、离线队列双入队。
 */
class SessionOfflineOnceTest {

    private static final class Fixture {

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

    /** 与 SessionResendSafetyTest 同款最小隧道：close/disconnect 走 BaseNetTunnel 真实路径 */
    private static final class TestTunnel extends TestTunnelFixture {

        final AtomicInteger writes;

        TestTunnel(AtomicInteger writes) {
            super(TestTunnelFixture.ActivePolicy.NOT_CLOSED, 7100, 7101);
            this.writes = writes;
        }

        @Override
        public MessageWriteFuture write(MessageAllocator allocator, MessageContent content) {
            writes.incrementAndGet();
            return null;
        }

        @Override
        public MessageWriteFuture write(Message message, MessageWriteFuture promise) {
            writes.incrementAndGet();
            return promise;
        }
    }

    private CommonSession newOnlineSession(Fixture fixture) throws Exception {
        CommonSession session = new CommonSession(fixture.context, fixture.tunnel);
        session.online(Certificates.createAuthenticated(11L, 777L, 888L, DefaultContactType.DEFAULT_USER));
        assertEquals(SessionStatus.ONLINE, session.getStatus());
        return session;
    }

    @Test
    @DisplayName("下线回调内关闭会话：不递归、下线事件总计一次")
    void closeInsideOfflineListenerIsSafe() throws Exception {
        Fixture fixture = new Fixture();
        CommonSession session = newOnlineSession(fixture);
        AtomicInteger offlineCount = new AtomicInteger();
        session.events().offlineWatch().addListener(ignored -> {
            offlineCount.incrementAndGet();
            session.close();
        });

        assertDoesNotThrow(session::offline, "下线回调中 close 不得递归放大");
        assertEquals(1, offlineCount.get(), "下线事件必须恰好一次");
        assertEquals(SessionStatus.CLOSE, session.getStatus());
    }

    @Test
    @DisplayName("通道断开后再关闭会话：离线通知总计一次")
    void disconnectThenCloseNotifiesOnce() throws Exception {
        Fixture fixture = new Fixture();
        CommonSession session = newOnlineSession(fixture);
        AtomicInteger offlineCount = new AtomicInteger();
        session.events().offlineWatch().addListener(ignored -> offlineCount.incrementAndGet());

        fixture.tunnel.close();   // 通道侧: onUnactivated → setOffline
        session.offline();        // 会话侧: offline → (tunnel 已关) setOffline
        session.close();

        assertEquals(1, offlineCount.get(), "同一会话的下线转换必须只派发一次");
    }

    @Test
    @DisplayName("兼容锚：正常上线→下线→关闭事件各恰一次")
    @SuppressWarnings("unchecked")
    void normalTransitionSequence() throws Exception {
        Fixture fixture = new Fixture();
        CommonSession session = new CommonSession(fixture.context, fixture.tunnel);
        AtomicInteger onlineCount = new AtomicInteger();
        AtomicInteger offlineCount = new AtomicInteger();
        AtomicInteger closeCount = new AtomicInteger();
        session.events().onlineWatch().addListener(ignored -> onlineCount.incrementAndGet());
        session.events().offlineWatch().addListener(ignored -> offlineCount.incrementAndGet());
        session.events().closeWatch().addListener(ignored -> closeCount.incrementAndGet());

        session.online(Certificates.createAuthenticated(12L, 778L, 889L, DefaultContactType.DEFAULT_USER));
        session.offline();
        session.close();
        session.close();

        assertEquals(1, onlineCount.get());
        assertEquals(1, offlineCount.get());
        assertEquals(1, closeCount.get(), "重复 close 不得二次派发关闭事件（既有幂等的回归锚）");
    }

}
