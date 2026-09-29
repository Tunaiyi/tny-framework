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
package com.tny.game.net.command.dispatcher;

import com.tny.game.net.application.*;
import com.tny.game.net.command.plugins.CommandPlugin;
import com.tny.game.net.message.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.AtomicInteger;

import static com.tny.game.net.command.dispatcher.RpcContextFixture.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 插件链失败语义（message-checking 规格 · 检查插件链的失败语义）。
 */
class PluginChainFailClosedTest {

    private final MockNetTunnel tunnel = new MockNetTunnel(NetAccessMode.SERVER);

    @Test
    void throwingFirstPluginInterceptsAndStopsChain() {
        AtomicInteger secondInvocations = new AtomicInteger();
        PluginChain chain = new PluginChain(holder(new ThrowingPlugin()));
        chain.append(new PluginChain(holder(new CountingPlugin(secondInvocations))));
        RpcInvokeContext context = context();

        chain.execute(tunnel, message(1L, System.currentTimeMillis()), context);

        assertTrue(context.isIntercept(), "插件异常必须转为拦截（fail-closed）");
        assertEquals(0, secondInvocations.get(), "异常后链上后续插件不得执行（修复前吞异常穿透，此断言应红）");
    }

    @Test
    void healthyChainPassesThroughInOrder() {
        AtomicInteger invocations = new AtomicInteger();
        PluginChain chain = new PluginChain(holder(new CountingPlugin(invocations)));
        chain.append(new PluginChain(holder(new CountingPlugin(invocations))));
        RpcInvokeContext context = context();

        chain.execute(tunnel, message(2L, System.currentTimeMillis()), context);

        assertEquals(2, invocations.get(), "正常插件必须全部依序执行");
        assertFalse(context.isIntercept(), "正常链不产生拦截");
    }

    @Test
    void alreadyInterceptedContextShortCircuitsChain() {
        AtomicInteger invocations = new AtomicInteger();
        PluginChain chain = new PluginChain(holder(new CountingPlugin(invocations)));
        RpcInvokeContext context = context();
        context.doneAndIntercept(NetResultCode.SERVER_NO_SUCH_PROTOCOL);

        chain.execute(tunnel, message(3L, System.currentTimeMillis()), context);

        assertEquals(0, invocations.get(), "已拦截上下文不得触发插件");
    }

    /** 抛运行时异常的桩插件（模拟校验环境故障） */
    static final class ThrowingPlugin implements CommandPlugin<Object> {
        @Override
        public Class<Object> getAttributesClass() {
            return Object.class;
        }

        @Override
        public void execute(Tunnel tunnel, Message message, RpcInvokeContext context, Object attribute) {
            throw new RuntimeException("fixture plugin failure");
        }
    }

    static final class CountingPlugin implements CommandPlugin<Object> {
        private final AtomicInteger counter;

        CountingPlugin(AtomicInteger counter) {
            this.counter = counter;
        }

        @Override
        public Class<Object> getAttributesClass() {
            return Object.class;
        }

        @Override
        public void execute(Tunnel tunnel, Message message, RpcInvokeContext context, Object attribute) {
            counter.incrementAndGet();
        }
    }

}
