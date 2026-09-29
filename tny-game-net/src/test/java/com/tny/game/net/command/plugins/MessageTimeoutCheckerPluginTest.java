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

import com.tny.game.net.application.*;
import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import static com.tny.game.net.command.dispatcher.RpcContextFixture.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 超时检查行为（message-checking 规格 · 请求超时检查）。
 */
class MessageTimeoutCheckerPluginTest {

    private final MessageTimeoutCheckerPlugin plugin = new MessageTimeoutCheckerPlugin();

    private final MockNetTunnel tunnel = new MockNetTunnel(NetAccessMode.SERVER);

    @Test
    void expiredRequestIsIntercepted() throws Exception {
        // 消息请求时间距今 6000ms，阈值 5000ms → 应拦截
        RpcInvokeContext context = context();
        plugin.execute(tunnel, message(1L, System.currentTimeMillis() - 6000L), context, 5000L);
        assertTrue(context.isIntercept(), "超期请求应被拦截");
    }

    @Test
    void freshRequestPassesThrough() throws Exception {
        // 请求时间距今 100ms，阈值 5000ms → 应放行
        RpcInvokeContext context = context();
        plugin.execute(tunnel, message(2L, System.currentTimeMillis() - 100L), context, 5000L);
        assertFalse(context.isIntercept(), "未超期请求不得被拦截（修复前恒拦截，此断言应红）");
    }

    @Test
    void nonPositiveThresholdSkipsCheck() throws Exception {
        RpcInvokeContext context = context();
        plugin.execute(tunnel, message(3L, System.currentTimeMillis() - 600000L), context, 0L);
        assertFalse(context.isIntercept(), "阈值非正数时不做超时判定");
    }

}
