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

import com.tny.game.net.command.dispatcher.*;
import com.tny.game.net.rpc.*;
import com.tny.game.net.transport.*;
import org.junit.jupiter.api.*;

import static com.tny.game.net.command.dispatcher.RpcContextFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 10（Wave-A）红灯基线：超时检查器 attribute 缺省（null）不得拦截、不得内部错误
 * （message-checking"缺省配置正确放行"契约；注解 attribute 默认 "@null" 即未声明）。
 */
class MessageTimeoutNullAttributeTest {

    private final MessageTimeoutCheckerPlugin plugin = new MessageTimeoutCheckerPlugin();

    private final MockNetTunnel tunnel = new MockNetTunnel(NetAccessMode.SERVER);

    @Test
    @DisplayName("attribute 为 null：放行且无内部异常（修复前拆箱 NPE→fail-closed 500）")
    void nullAttributePassesThrough() throws Exception {
        RpcInvokeContext context = context();
        assertDoesNotThrow(() -> plugin.execute(tunnel, message(1L, 0L), context, null));
        assertFalse(context.isIntercept(), "未声明阈值不得拦截");
    }

    @Test
    @DisplayName("兼容锚：attribute 为 0 依旧放行")
    void zeroAttributePassesThrough() throws Exception {
        RpcInvokeContext context = context();
        plugin.execute(tunnel, message(2L, 0L), context, 0L);
        assertFalse(context.isIntercept());
    }

    @Test
    @DisplayName("兼容锚：正常阈值仍拦截超期请求")
    void positiveThresholdStillWorks() throws Exception {
        RpcInvokeContext context = context();
        plugin.execute(tunnel, message(3L, System.currentTimeMillis() - 6000L), context, 5000L);
        assertTrue(context.isIntercept());
    }

}
