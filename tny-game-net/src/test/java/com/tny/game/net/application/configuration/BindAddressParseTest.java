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
package com.tny.game.net.application.configuration;

import org.junit.jupiter.api.*;

import java.net.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 23（Wave-C）红灯基线：监听地址配置笔误以可诊断异常启动期失败
 * （修复前缺端口 AIOOBE、坏端口 NumberUtils 静默 0）。
 */
class BindAddressParseTest {

    @Test
    @DisplayName("缺端口：IllegalArgumentException 指明格式")
    void missingPortRejected() {
        CommonServerBootstrapSetting setting = new CommonServerBootstrapSetting();
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> setting.setBindAddress("127.0.0.1"));
        assertTrue(error.getMessage().contains("host:port"));
    }

    @Test
    @DisplayName("端口越界：拒绝而非静默 0 端口")
    void badPortRejected() {
        CommonServerBootstrapSetting setting = new CommonServerBootstrapSetting();
        assertThrows(IllegalArgumentException.class, () -> setting.setBindAddress("127.0.0.1:abc"));
        assertThrows(IllegalArgumentException.class, () -> setting.setBindAddress("127.0.0.1:70000"));
    }

    @Test
    @DisplayName("兼容锚：合法 host:port 正常解析")
    void validAddressParsed() {
        CommonServerBootstrapSetting setting = new CommonServerBootstrapSetting();
        setting.setBindAddress("127.0.0.1:7000");
        InetSocketAddress address = setting.bindAddress();
        assertEquals(7000, address.getPort());
        assertEquals("127.0.0.1", address.getHostString());
    }

}
