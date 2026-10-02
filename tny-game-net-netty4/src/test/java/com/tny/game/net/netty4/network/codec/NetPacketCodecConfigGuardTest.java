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
