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
package com.tny.game.net.command.plugins;

import com.tny.game.common.context.AttrKeys;
import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.session.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import static com.tny.game.net.command.dispatcher.RpcContextFixture.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 重放检查行为（message-checking 规格 · 重放消息检查）。
 */
class MessageSequenceCheckerPluginTest {

    private final MessageSequenceCheckerPlugin plugin = new MessageSequenceCheckerPlugin();

    @Test
    void newerMessagePassesAndAdvancesWatermark() throws Exception {
        TestSupport support = authenticatedSupport();
        RpcInvokeContext context = context();
        support.pluginExecute(plugin, context, message(10L, System.currentTimeMillis()));
        assertFalse(context.isIntercept(), "编号 10（水位 0）应放行");
        support.assertWatermark(10, "放行后水位应推进为消息编号（修复前写旧值，此断言应红）");
    }

    @Test
    void replayedMessageIsInterceptedAndWatermarkKept() throws Exception {
        TestSupport support = authenticatedSupport();
        RpcInvokeContext first = context();
        support.pluginExecute(plugin, first, message(10L, System.currentTimeMillis()));
        RpcInvokeContext replay = context();
        support.pluginExecute(plugin, replay, message(10L, System.currentTimeMillis()));
        assertTrue(replay.isIntercept(), "重放消息（编号不大于水位）应拦截（修复前水位恒 0，此断言应红）");
        support.assertWatermark(10, "拦截不得改变水位");
    }

    @Test
    void unauthenticatedConnectionIsExempt() throws Exception {
        MockNetTunnel tunnel = new MockNetTunnel(NetAccessMode.SERVER);
        tunnel.bind(new CommonSessionKeeperForTest().anonymousSession());
        RpcInvokeContext context = context();
        plugin.doExecute(tunnel, message(0L, System.currentTimeMillis()), context);
        assertFalse(context.isIntercept(), "未认证连接豁免重放判定");
    }

    /** 每测试独立会话+隧道（水位挂在 session attributes 上） */
    private TestSupport authenticatedSupport() {
        return new TestSupport();
    }

    private static final class TestSupport {
        final MockNetTunnel tunnel;
        final NetSession session;

        TestSupport() {
            this.session = new CommonSessionKeeperForTest().authenticatedSession();
            this.tunnel = new MockNetTunnel(NetAccessMode.SERVER);
            this.tunnel.bind(this.session);
        }

        void pluginExecute(MessageSequenceCheckerPlugin plugin, RpcInvokeContext context, NetMessage message) {
            plugin.doExecute(tunnel, message, context);
        }

        void assertWatermark(int expected, String message) {
            Object value = this.session.attributes().getAttribute(
                    AttrKeys.key(MessageSequenceCheckerPlugin.class, "CHECK_MESSAGE_ID"), 0);
            assertEquals(expected, value, message);
        }
    }

    static final ContactType PLAYER = new ContactType() {
        @Override
        public String getGroup() {
            return "player";
        }

        @Override
        public int id() {
            return 1;
        }

        @Override
        public String name() {
            return getGroup();
        }
    };

    /** 会话构造辅助（认证/匿名两态） */
    static final class CommonSessionKeeperForTest {
        NetSession authenticatedSession() {
            return new MockNetSession(Certificates.createAuthenticated(1L, 100L, 200L, PLAYER), NetAccessMode.SERVER);
        }

        NetSession anonymousSession() {
            return new MockNetSession(Certificates.anonymous(), NetAccessMode.SERVER);
        }
    }

}
