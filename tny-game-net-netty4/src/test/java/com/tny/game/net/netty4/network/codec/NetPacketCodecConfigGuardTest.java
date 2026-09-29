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
package com.tny.game.net.netty4.network.codec;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 3：编解码装配单元（prepareStart）必须先于单元解析完成安全配置校验，
 * 使缺密钥的部署以明确原因启动失败（net-protocol"安全配置的启动期完备性校验"）。
 */
class NetPacketCodecConfigGuardTest {

    @Test
    @DisplayName("加密启用缺密钥：prepareStart 以 security keys 明确失败")
    void missingKeysFailPrepareStartWithDiagnostic() {
        NetPacketCodecSetting setting = new NetPacketCodecSetting();
        setting.setEncryptEnable(true);
        NetPacketV1Decoder decoder = new NetPacketV1Decoder(setting);

        IllegalStateException error = assertThrows(IllegalStateException.class, decoder::prepareStart);
        assertTrue(error.getMessage().contains("security keys"),
                "启动失败原因必须指向缺失的密钥配置，而非依赖解析噪声: " + error.getMessage());
    }

}
