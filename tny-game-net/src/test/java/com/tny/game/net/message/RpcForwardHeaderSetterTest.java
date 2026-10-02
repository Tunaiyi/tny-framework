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
package com.tny.game.net.message;

import com.tny.game.net.application.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 22（Wave-C）红灯基线：转发头装配 setter 必须使用入参（修复前字段自引用，转发者信息恒 null）。
 */
class RpcForwardHeaderSetterTest {

    private static RpcServicer servicer(int serverId) {
        RpcServiceType serviceType = mock(RpcServiceType.class);
        when(serviceType.id()).thenReturn(42);
        RpcServicer servicer = mock(RpcServicer.class);
        when(servicer.getServiceType()).thenReturn(serviceType);
        when(servicer.getServerId()).thenReturn(serverId);
        return servicer;
    }

    @Test
    @DisplayName("setFromForwarder/setToForwarder 写入参数对应的转发者")
    void forwarderSettersUseParameter() {
        RpcForwardHeader header = new RpcForwardHeader();
        RpcServicer from = servicer(11);
        RpcServicer to = servicer(22);

        header.setFromForwarder(from);
        header.setToForwarder(to);

        assertNotNull(header.getFromForwarder(), "修复前自引用导致恒 null");
        assertNotNull(header.getToForwarder());
        assertSame(from.getServiceType(), header.getFromForwarder().getServiceType());
        assertSame(to.getServiceType(), header.getToForwarder().getServiceType());
    }

    @Test
    @DisplayName("兼容锚：setFrom/setTo 语义寻址字段不受影响")
    void fromToUnchanged() {
        RpcForwardHeader header = new RpcForwardHeader();
        RpcServicer from = servicer(33);
        RpcServicer to = servicer(44);
        header.setFrom(from);
        header.setTo(to);
        assertSame(from.getServiceType(), header.getFrom().getServiceType());
        assertSame(to.getServiceType(), header.getTo().getServiceType());
        assertNull(header.getFromForwarder(), "from/to 不再误写转发者字段");
    }

}
