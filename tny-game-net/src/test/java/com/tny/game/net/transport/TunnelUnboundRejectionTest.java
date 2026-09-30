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

import com.tny.game.net.command.dispatcher.RpcContextFixture;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.NetAccessMode;
import com.tny.game.net.session.*;
import org.junit.jupiter.api.*;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 通道层未绑定会话的可诊断拒绝（net-tunnel 规格）。
 */
class TunnelUnboundRejectionTest {

    /** ① 未绑定 receive：必须返回 false 且不抛 NPE（当前实现 NPE，本用例应红） */
    @Test
    void unboundReceiveIsRejectedWithoutCrash() {
        TestTunnelFixture tunnel = new TestTunnelFixture(TestTunnelFixture.ActivePolicy.NOT_CLOSED, 7100, 7000);
        NetMessage message = RpcContextFixture.message(1L, System.currentTimeMillis());

        boolean accepted = assertDoesNotThrow(() -> tunnel.receive(message),
                "未绑定会话的接收必须显式拒绝而非空指针（当前应红）");
        assertFalse(accepted, "拒绝语义返回 false（当前应红）");
    }

    /** ② 拒绝不损伤通道：绑定会话后消息正常受理（当前前置拒绝即 NPE，本用例应红） */
    @Test
    void channelUnharmedAfterRejection() {
        TestTunnelFixture tunnel = new TestTunnelFixture(TestTunnelFixture.ActivePolicy.NOT_CLOSED, 7100, 7000);
        assertDoesNotThrow(() -> tunnel.receive(RpcContextFixture.message(1L, System.currentTimeMillis())),
                "前置：首次未绑定拒绝不得抛异常（当前应红）");

        tunnel.resetSession(new CountingSession());

        assertTrue(tunnel.receive(RpcContextFixture.message(2L, System.currentTimeMillis())),
                "绑定后消息应正常受理");
        assertEquals(1, ((CountingSession) tunnel.getSession()).handled.get(), "会话恰好收到一次投递");
    }

    /** 记录型会话（receive 受理计数，行为对齐生产 session.receive 恒 true/throw 的核实语义） */
    private static final class CountingSession extends MockNetSession {
        final AtomicInteger handled = new AtomicInteger();

        CountingSession() {
            super(Certificates.anonymous(), NetAccessMode.SERVER);
        }

        @Override
        public boolean receive(com.tny.game.net.command.dispatcher.RpcEnterContext rpcContext) {
            handled.incrementAndGet();
            return true;
        }
    }

}
