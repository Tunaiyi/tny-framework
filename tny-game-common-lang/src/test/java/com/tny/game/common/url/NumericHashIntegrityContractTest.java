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

package com.tny.game.common.url;

import org.junit.jupiter.api.*;

import java.lang.reflect.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * numeric-hash-integrity 补做契约钉桩（verify 裁决 REAL_GAP 第 8 项）：
 * URL.valueOf / setAddress / appendDefaultPort 对尾冒号与多冒号裸 IPv6 显式受控失败。
 */
public class NumericHashIntegrityContractTest {

    // ==================== 向后兼容（正常路径） ====================

    @Test
    public void regularHostPortParsesAsBefore() {
        URL url = URL.valueOf("dubbo://10.0.0.1:20880/org.tny.Foo?side=provider&weight=100");
        assertEquals("dubbo", url.getScheme());
        assertEquals("10.0.0.1", url.getHost());
        assertEquals(20880, url.getPort());
        assertEquals("org.tny.Foo", url.getPath());
        assertEquals("provider", url.getParameter("side", ""));
        assertEquals("10.0.0.1:20880", url.getAddress());
    }

    @Test
    public void plainHostWithoutPortStillParses() {
        URL url = URL.valueOf("10.0.0.1");
        assertEquals("10.0.0.1", url.getHost());
        assertEquals(0, url.getPort());
        assertEquals("10.0.0.1", url.getAddress());
    }

    @Test
    public void userInfoAndFileSchemeStillParse() {
        URL url = URL.valueOf("amqp://user:pass@10.0.0.1:5672/vhost");
        assertEquals("user", url.getUsername());
        assertEquals("pass", url.getPassword());
        assertEquals("10.0.0.1", url.getHost());
        assertEquals(5672, url.getPort());
        URL file = URL.valueOf("file:/path/to/file.txt");
        assertEquals("file", file.getScheme());
        assertEquals("path/to/file.txt", file.getPath());
    }

    @Test
    public void backupUrlsChainStillWorks() {
        URL url = URL.valueOf("dubbo://1.1.1.1:20880?backup=2.2.2.2:20881,3.3.3.3:20882");
        assertEquals(3, url.getBackupUrls().size());
        assertEquals("2.2.2.2", url.getBackupUrls().get(1).getHost());
        assertEquals(20881, url.getBackupUrls().get(1).getPort());
    }

    // ==================== valueOf 显式失败路径 ====================

    private static void assertControlledRejection(Runnable parsing, String expectedTextInMessage) {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, parsing::run);
        assertSame(IllegalArgumentException.class, thrown.getClass(), "必须受控校验失败，不得外泄裸数字解析异常");
        assertTrue(thrown.getMessage().contains(expectedTextInMessage), "失败信息应指出问题文本");
    }

    @Test
    public void trailingColonRejected() {
        assertControlledRejection(() -> URL.valueOf("dubbo://10.0.0.1:"), "10.0.0.1:");
        assertControlledRejection(() -> URL.valueOf("10.0.0.1:"), "10.0.0.1:");
    }

    @Test
    public void bareIpv6AndMultiColonRejected() {
        assertControlledRejection(() -> URL.valueOf("2401:da8:8001::1:20880"), "2401:da8:8001::1:20880");
        assertControlledRejection(() -> URL.valueOf("rpc://[::1]:20880"), "[::1]:20880");
        assertControlledRejection(() -> URL.valueOf("1.2.3.4:20880:9090"), "1.2.3.4:20880:9090");
    }

    @Test
    public void nonNumericPortRejectedControlled() {
        assertControlledRejection(() -> URL.valueOf("myhost:notaport"), "notaport");
    }

    // ==================== setAddress 同规则 ====================

    @Test
    public void setAddressNormalPathsUnchanged() {
        URL base = URL.valueOf("dubbo://1.1.1.1:20880");
        URL moved = base.setAddress("2.2.2.2:20881");
        assertEquals("2.2.2.2", moved.getHost());
        assertEquals(20881, moved.getPort());
        URL hostOnly = base.setAddress("2.2.2.2");
        assertEquals("2.2.2.2", hostOnly.getHost());
        assertEquals(20880, hostOnly.getPort(), "无冒号形态保持既有端口语义");
    }

    @Test
    public void setAddressAmbiguousFormsRejected() {
        URL base = URL.valueOf("dubbo://1.1.1.1:20880");
        assertControlledRejection(() -> base.setAddress("2.2.2.2:"), "2.2.2.2:");
        assertControlledRejection(() -> base.setAddress("2401:da8::1"), "2401:da8::1");
        assertControlledRejection(() -> base.setAddress("2.2.2.2:port"), "port");
    }

    // ==================== appendDefaultPort 同规则（私有入口反射钉桩） ====================

    private static String appendDefaultPort(URL url, String address, int defaultPort) throws Exception {
        Method method = URL.class.getDeclaredMethod("appendDefaultPort", String.class, int.class);
        method.setAccessible(true);
        return (String) method.invoke(url, address, defaultPort);
    }

    @Test
    public void appendDefaultPortNormalPathsUnchanged() throws Exception {
        URL url = URL.valueOf("dubbo://1.1.1.1:20880");
        assertEquals("10.0.0.1:20880", appendDefaultPort(url, "10.0.0.1", 20880));
        assertEquals("10.0.0.1:20880", appendDefaultPort(url, "10.0.0.1:0", 20880));
        assertEquals("10.0.0.1:20881", appendDefaultPort(url, "10.0.0.1:20881", 20880));
        assertEquals("10.0.0.1:", appendDefaultPort(url, "10.0.0.1:", 0), "默认端口不为正数时维持原直通语义");
    }

    @Test
    public void appendDefaultPortAmbiguousFormsRejectedControlled() throws Exception {
        URL url = URL.valueOf("dubbo://1.1.1.1:20880");
        Method method = URL.class.getDeclaredMethod("appendDefaultPort", String.class, int.class);
        method.setAccessible(true);
        for (String bad : new String[]{"10.0.0.1:", "a:b:c", "10.0.0.1:notaport"}) {
            InvocationTargetException thrown = assertThrows(InvocationTargetException.class,
                                                            () -> method.invoke(url, bad, 20880));
            assertSame(IllegalArgumentException.class, thrown.getCause().getClass(),
                       "私有入口同样必须受控失败，不得外泄裸数字解析异常：" + bad);
            assertTrue(thrown.getCause().getMessage().contains(bad.substring(0, Math.min(bad.length(), 5))));
        }
    }

}
