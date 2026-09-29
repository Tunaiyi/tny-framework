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
package com.tny.game.net.transport;

import com.tny.game.net.application.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;
import org.junit.jupiter.api.*;

import java.net.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 17（Wave-C）红灯基线：通道事件全序（激活→失活→关闭）且各恰一次（net-tunnel"事件全序"契约）。
 * 修复前：客户端形态 disconnect 嵌套 close 使 close 先于 unactivated 观察、session.onUnactivated 双发。
 */
class TunnelEventOrderTest {

    private static class TestTunnel extends BaseNetTunnel<NetSession> {

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
            return new InetSocketAddress(7300);
        }

        @Override
        public InetSocketAddress getLocalAddress() {
            return new InetSocketAddress(7301);
        }

        @Override
        public MessageWriteFuture write(MessageAllocator allocator, MessageContent content) throws com.tny.game.net.exception.NetException {
            return null;
        }

        @Override
        public MessageWriteFuture write(Message message, MessageWriteFuture promise) {
            return promise;
        }
    }

    /** 复现客户端形态：onDisconnected 内再 close（原双通知/倒序触发点） */
    private static class ClientLikeTunnel extends TestTunnel {
        @Override
        protected void onDisconnected() {
            this.close();
        }
    }

    private final List<String> sequence = Collections.synchronizedList(new ArrayList<>());

    private <T extends TestTunnel> T recording(T tunnel, NetSession session) {
        tunnel.events().activateWatch().addListener(ignored -> sequence.add("activated"));
        tunnel.events().unactivatedWatch().addListener(ignored -> sequence.add("unactivated"));
        tunnel.events().closeWatch().addListener(ignored -> sequence.add("closed"));
        doAnswer(invocation -> {
            sequence.add("sessionUnactivated");
            return null;
        }).when(session).onUnactivated(any(NetTunnel.class));
        return tunnel;
    }

    @Test
    @DisplayName("正常路径：激活→失活→关闭各一次")
    void normalCloseSequence() {
        NetSession session = mock(NetSession.class);
        TestTunnel tunnel = recording(new TestTunnel(), session);
        assertTrue(tunnel.bind(session));
        assertTrue(tunnel.open());

        tunnel.close();

        assertEquals(List.of("activated", "sessionUnactivated", "unactivated", "closed"), sequence);
        verify(session, times(1)).onUnactivated(tunnel);
    }

    @Test
    @DisplayName("断开链上嵌套关闭：失活不双发、关闭不倒挂失活之前")
    void disconnectWithNestedCloseKeepsOrderAndOnce() {
        NetSession session = mock(NetSession.class);
        ClientLikeTunnel tunnel = recording(new ClientLikeTunnel(), session);
        assertTrue(tunnel.bind(session));
        assertTrue(tunnel.open());
        sequence.clear();

        tunnel.disconnect();
        tunnel.close(); // 幂等：不得再有第二次事件

        assertEquals(List.of("sessionUnactivated", "unactivated", "closed"), sequence,
                "修复前为 [closed 先于 unactivated] 且 sessionUnactivated 双发");
        verify(session, times(1)).onUnactivated(tunnel);
    }

}
