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
