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
