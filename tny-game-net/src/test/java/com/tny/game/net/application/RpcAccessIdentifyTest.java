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
package com.tny.game.net.application;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 组 4（Wave-A）红灯基线：接入身份十进制拼位的双侧界校验与回代验证。
 * 修复前负 index 借位导致 serverId 静默 -1、index 变 9999（身份漂移到错误节点）。
 */
class RpcAccessIdentifyTest {

    private static final long SERVICE_TYPE_SIZE = 1_000_000_000_000_000L; // type 槽 = 10^4(index) * 10^11(serviceId) = 10^15

    private static RpcServiceType serviceType(int id) {
        RpcServiceType type = mock(RpcServiceType.class);
        when(type.id()).thenReturn(id);
        return type;
    }

    @Test
    @DisplayName("兼容锚：合法拼位结果与旧公式逐位一致（wire 不变）")
    void legacyFormulaUnchanged() {
        long id = RpcAccessIdentify.formatId(serviceType(3), 5, 9999);
        assertEquals(3L * SERVICE_TYPE_SIZE + 5L * 10000 + 9999, id);
        assertEquals(5, RpcAccessIdentify.parseServerId(id), "回读 serverId 不变");
    }

    @Test
    @DisplayName("负 index 拼装：立即拒绝（不得借位漂移）")
    void negativeIndexRejected() {
        assertThrows(IllegalArgumentException.class, () -> RpcAccessIdentify.formatId(serviceType(3), 5, -1));
        assertThrows(IllegalArgumentException.class, () -> new RpcAccessIdentify(serviceType(3), 5, -1));
    }

    @Test
    @DisplayName("负 serverId 拼装：立即拒绝（不得吞掉 type 位）")
    void negativeServerIdRejected() {
        assertThrows(IllegalArgumentException.class, () -> RpcAccessIdentify.formatId(serviceType(3), -1, 0));
        assertThrows(IllegalArgumentException.class, () -> new RpcAccessIdentify(serviceType(3), -1, 0));
    }

    @Test
    @DisplayName("兼容锚：越上界 index 构造即拒（修复前后一致）")
    void oversizedIndexStillRejected() {
        assertThrows(IllegalArgumentException.class, () -> new RpcAccessIdentify(serviceType(3), 5, 10000));
    }

    @Test
    @DisplayName("解析未注册的服务类型：以可诊断异常拒绝而非静默 null 构造")
    void unknownServiceTypeParseRejected() {
        long unknownTypeId = 999_000_000_000_000_000L + 5L * 10000 + 1; // type=999，注册表必然无此类型
        assertThrows(IllegalArgumentException.class, () -> new RpcAccessIdentify(unknownTypeId));
    }

}
