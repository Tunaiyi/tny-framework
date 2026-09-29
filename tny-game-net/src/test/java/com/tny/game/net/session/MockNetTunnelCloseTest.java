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
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试桩 {@link MockNetTunnel} 的关闭幂等（对齐真实隧道的"先置态后回调"关序）。
 * 当前实现 close→disconnect→onUnactivated→close 无守卫递归，匿名会话场景必栈溢出（应红）。
 */
class MockNetTunnelCloseTest {

    @Test
    void closingAnonymousBoundSessionTerminates() {
        MockNetTunnel tunnel = new MockNetTunnel(NetAccessMode.SERVER);
        CommonSession session = new CommonSession(new StubContext(), tunnel);

        assertDoesNotThrow(session::close, "关闭必须收敛，不得递归溢出（当前应红）");
        assertSame(SessionStatus.CLOSE, session.getStatus());

        assertDoesNotThrow(session::close, "重复关闭安全（幂等）");
    }

    /** 最小会话上下文桩（与 SessionResendSafetyTest 同型） */
    private static final class StubContext implements SessionContext {
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
                    command.run();
                }
            };
        }
    }

}
