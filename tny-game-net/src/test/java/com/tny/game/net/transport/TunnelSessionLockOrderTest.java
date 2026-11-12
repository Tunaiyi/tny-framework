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

import com.tny.game.net.application.*;
import com.tny.game.net.session.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.concurrent.locks.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 16（Wave-C）红灯基线：隧道锁与会话锁不得 ABBA——
 * 不变量断言"隧道在任何持锁临界区内不得触达会话回调"（修复前 disconnect→onDisconnected→close
 * 嵌套在外层 statusLock 中调用 session.onUnactivated，与登录路径 session→tunnel 反向成环）。
 */
class TunnelSessionLockOrderTest {

    private static ReentrantLock tunnelLock(BaseNetTunnel<?> tunnel) throws Exception {
        Field field = BaseNetTunnel.class.getDeclaredField("statusLock");
        field.setAccessible(true);
        return (ReentrantLock) field.get(tunnel);
    }

    @Test
    @DisplayName("断开链路上所有会话回调必须发生在隧道锁之外")
    void disconnectNeverInvokesSessionCallbacksUnderTunnelLock() throws Exception {
        NetSession session = mock(NetSession.class);
        AtomicBooleanHolder holder = new AtomicBooleanHolder();
        ClientTransportTunnel<MessageTransport> tunnel = null;
        MessageTransport transport = mock(MessageTransport.class);
        when(transport.isActive()).thenReturn(true);
        final ClientTransportTunnel<MessageTransport>[] ref = new ClientTransportTunnel[1];
        doAnswer(invocation -> {
            holder.set(tunnelLock(ref[0]).isHeldByCurrentThread());
            return null;
        }).when(session).onUnactivated(any(NetTunnel.class));
        ref[0] = tunnel = new ClientTransportTunnel<>(1L, transport, session, mock(NetworkContext.class), null);

        assertTrue(tunnel.open(), "前置：模拟通道打开");

        tunnel.disconnect();

        assertTrue(holder.wasCalled(), "会话回调必须被触达（否则本测试失去意义）");
        assertFalse(holder.first(), "首次会话回调时隧道锁必须已释放（修复前于 onDisconnected 嵌套持锁触达，应红）");
    }

    static final class AtomicBooleanHolder {

        private boolean called;

        private boolean first;

        void set(boolean held) {
            if (!called) {
                this.first = held;
                this.called = true;
            }
        }

        boolean wasCalled() {
            return called;
        }

        boolean first() {
            return first;
        }
    }

}
