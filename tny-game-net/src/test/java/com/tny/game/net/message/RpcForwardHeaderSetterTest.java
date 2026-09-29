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
