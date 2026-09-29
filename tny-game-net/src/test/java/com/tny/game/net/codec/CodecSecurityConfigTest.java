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
package com.tny.game.net.codec;

import org.junit.jupiter.api.*;

import java.lang.reflect.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 3（Wave-A）红灯基线：加密/校验配置的启动期完备性与密钥派生的数值安全。
 * 对应 net-protocol"安全配置的启动期完备性校验"契约与任务 3.1。
 */
class CodecSecurityConfigTest {

    @Test
    @DisplayName("加密启用而未配密钥：启动期检查以可诊断异常失败")
    void missingKeysWithEncryptFailsFast() throws Exception {
        DataPackCodecOptions options = new DataPackCodecOptions();
        options.setEncryptEnable(true);

        Method check = options.getClass().getMethod("checkSecurityConfig");
        InvocationTargetException error = assertThrows(InvocationTargetException.class, () -> {
            try {
                check.invoke(options);
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
        });
        assertInstanceOf(IllegalStateException.class, error.getCause());
        assertTrue(error.getCause().getMessage().contains("security keys"), "应指明缺失密钥配置");
    }

    @Test
    @DisplayName("校验启用而未配密钥：同样启动期失败")
    void missingKeysWithVerifyFailsFast() throws Exception {
        DataPackCodecOptions options = new DataPackCodecOptions();
        options.setVerifyEnable(true);
        Method check = options.getClass().getMethod("checkSecurityConfig");
        assertThrows(InvocationTargetException.class, () -> {
            try {
                check.invoke(options);
            } catch (IllegalAccessException e) {
                throw new AssertionError(e);
            }
        });
    }

    @Test
    @DisplayName("密钥齐备或未启用安全项：检查通过")
    void validOrDisabledConfigPasses() throws Exception {
        DataPackCodecOptions enabled = new DataPackCodecOptions();
        enabled.setEncryptEnable(true).setSecurityKeys(new String[]{"key-a", "key-b"});
        enabled.getClass().getMethod("checkSecurityConfig").invoke(enabled);

        DataPackCodecOptions disabled = new DataPackCodecOptions();
        disabled.getClass().getMethod("checkSecurityConfig").invoke(disabled);
    }

    @Test
    @DisplayName("包号 int 溢出为负：取键下标不得越界（floorMod）")
    void negativePacketNumberIndexSafe() {
        DataPackCodecOptions options = new DataPackCodecOptions();
        options.setSecurityKeys(new String[]{"a", "b", "c"});

        byte[] negative = options.getSecurityKeyBytes(-1);
        byte[] expected = options.getSecurityKeyBytes(2); // floorMod(-1,3)==2
        assertArrayEquals(expected, negative);
        assertArrayEquals(options.getSecurityKeyBytes(Integer.MIN_VALUE),
                          options.getSecurityKeyBytes(Math.floorMod(Integer.MIN_VALUE, 3)));
    }

    @Test
    @DisplayName("运行期重设密钥：派生字节缓存必须同步失效")
    void setSecurityKeysInvalidatesDerivedCache() {
        DataPackCodecOptions options = new DataPackCodecOptions();
        options.setSecurityKeys(new String[]{"first"});
        assertArrayEquals("first".getBytes(), options.getSecurityKeyBytes(0));

        options.setSecurityKeys(new String[]{"second"});
        assertArrayEquals("second".getBytes(), options.getSecurityKeyBytes(0),
                          "重设后不得继续使用旧密钥的派生缓存");
    }

    @Test
    @DisplayName("未配密钥取键：以明确异常替代除零崩溃")
    void emptyKeysThrowsDiagnosticNotArithmetic() {
        DataPackCodecOptions options = new DataPackCodecOptions();
        IllegalStateException error = assertThrows(IllegalStateException.class, () -> options.getSecurityKeyBytes(1));
        assertTrue(error.getMessage().contains("security keys"));
    }

}
