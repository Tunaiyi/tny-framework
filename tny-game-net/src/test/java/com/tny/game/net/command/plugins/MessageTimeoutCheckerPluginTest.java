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
