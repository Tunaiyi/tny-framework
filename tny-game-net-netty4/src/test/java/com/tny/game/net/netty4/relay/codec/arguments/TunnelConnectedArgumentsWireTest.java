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
package com.tny.game.net.netty4.relay.codec.arguments;

import com.tny.game.net.netty4.relay.codec.arguments.protobuf.*;
import com.tny.game.net.relay.packet.arguments.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 12（Wave-B）：TunnelConnectedArguments 构造参数互换的"自抵消修复"回归锚。
 * 本地 getter 修正（必红→绿），线上 proto 槽位语义逐字节保持不变（修复前后同绿）——
 * 任何一侧的单边"顺手修正"都会破坏新旧混跑的隧道识别。
 */
class TunnelConnectedArgumentsWireTest {

    private static final long INSTANCE_ID = 111L;
    private static final long TUNNEL_ID = 222L;

    @Test
    @DisplayName("本地 getter 语义：ofResult(instanceId, tunnelId) 按参数名返回")
    void localGettersMatchParameterNames() {
        TunnelConnectedArguments arguments = TunnelConnectedArguments.ofResult(INSTANCE_ID, TUNNEL_ID, true);
        assertEquals(INSTANCE_ID, arguments.getInstanceId(), "构造器 super 形参序与父类颠倒（修复前必红）");
        assertEquals(TUNNEL_ID, arguments.getTunnelId());
    }

    @Test
    @DisplayName("wire 槽位锚：proto 槽 1 恒承载历史语义（=现网隧道标识），不得翻转")
    void protoSlotsKeepLegacyWireSemantics() {
        TunnelConnectedArguments arguments = TunnelConnectedArguments.ofResult(INSTANCE_ID, TUNNEL_ID, false);
        TunnelConnectedArgumentsProto proto = new TunnelConnectedArgumentsProto(arguments);
        // 历史线上形态：instanceId 槽写入的是"互换后 getter"的 getInstanceId()==TUNNEL_ID
        assertEquals(TUNNEL_ID, proto.getInstanceId(), "槽位值翻转会破坏新旧混跑（回归锚，必须恒绿）");
        assertEquals(INSTANCE_ID, proto.getTunnelId(), "同上");
        assertFalse(proto.isResult());
    }

    @Test
    @DisplayName("同版本往返：编码→解码标识不漂移")
    void roundTripStable() {
        TunnelConnectedArguments arguments = TunnelConnectedArguments.ofResult(INSTANCE_ID, TUNNEL_ID, true);
        TunnelConnectedArgumentsProto proto = new TunnelConnectedArgumentsProto(arguments);
        TunnelConnectedArguments decoded = proto.toArguments();
        assertEquals(INSTANCE_ID, decoded.getInstanceId());
        assertEquals(TUNNEL_ID, decoded.getTunnelId());
        assertTrue(decoded.getResult());
    }

}
