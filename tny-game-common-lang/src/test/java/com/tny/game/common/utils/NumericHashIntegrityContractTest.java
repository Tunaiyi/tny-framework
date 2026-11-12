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

package com.tny.game.common.utils;

import com.tny.game.common.type.*;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.nio.charset.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * numeric-hash-integrity 补做契约钉桩（verify 裁决 REAL_GAP 第 4/5/6 项）：
 * crcStringHash32 的 UTF-8 字节定义域、murmur1 尾字节无符号折叠、ObjectAide 引用令牌直接父层取型。
 */
public class NumericHashIntegrityContractTest {

    // ==================== 4. crcStringHash32 ====================

    /**
     * 修复前（UTF-16 码元索引）实测钉桩：纯 ASCII 输入修复后必须逐位一致。
     */
    private static void assertCrcAsciiGolden() {
        assertEquals(0x0, HashAide.crcStringHash32(""));
        assertEquals(0x4db26158, HashAide.crcStringHash32("a"));
        assertEquals(0xc72d21eb, HashAide.crcStringHash32("hello"));
        assertEquals(0x5f3a486c, HashAide.crcStringHash32("Hello, World! 123"));
        assertEquals(0x7d53516, HashAide.crcStringHash32("0123456789"));
        assertEquals(0xc4da515, HashAide.crcStringHash32("dubbo://10.0.0.1:20880/org.tny.Foo?side=provider"));
        assertEquals(0x5c47cb51, HashAide.crcStringHash32("The quick brown fox"));
    }

    private static int crcByteSequenceReference(String data) throws Exception {
        Field tableField = HashAide.class.getDeclaredField("crcTable");
        tableField.setAccessible(true);
        int[] table = (int[]) tableField.get(null);
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        int hash = bytes.length;
        for (byte value : bytes) {
            hash = (hash >> 8) ^ table[(hash & 0xff) ^ (value & 0xff)];
        }
        return hash;
    }

    @Test
    public void crcAsciiResultsUnchanged() {
        assertCrcAsciiGolden();
    }

    @Test
    public void crcNonAsciiHasDeterministicValueInUtf8ByteDomain() throws Exception {
        String[] samples = {"中文", "a😀b", "配置键名", "Ω≈ç√∫", "", "a", "混合mix文本"};
        for (String sample : samples) {
            int first = HashAide.crcStringHash32(sample);
            int second = HashAide.crcStringHash32(sample);
            assertEquals(first, second, "同一文本哈希跨调用必须确定");
            assertEquals(crcByteSequenceReference(sample), first, "定义域必须是 UTF-8 字节序列逐字节无符号索引");
        }
        assertNotEquals(HashAide.crcStringHash32("中文"), HashAide.crcStringHash32("文仲"), "不同文本按哈希值区分");
    }

    // ==================== 5. murmur1StringHash32 ====================

    /**
     * 修复前（ASCII 输入实测）钉桩：ASCII 结果修复后逐位一致。
     */
    private static void assertMurmur1AsciiGolden() {
        assertEquals(0x3ec4a4f1, HashAide.murmur1StringHash32("a"));
        assertEquals(0x676c29a9, HashAide.murmur1StringHash32("ab"));
        assertEquals(0x66fb2f8b, HashAide.murmur1StringHash32("abc"));
        assertEquals(0x7d174a97, HashAide.murmur1StringHash32("abcd"));
        assertEquals(0x54eb1368, HashAide.murmur1StringHash32("abcde"));
        assertEquals(0x1242ae23, HashAide.murmur1StringHash32("abcdefghij"));
        assertEquals(0x729abf72, HashAide.murmur1StringHash32("id"));
        assertEquals(0x33881d5a, HashAide.murmur1StringHash32("name1234"));
        assertEquals(0x1833e644, HashAide.murmur1StringHash32("0"));
    }

    /**
     * 尾字节无符号折叠参考实现（对齐主循环与 murmur2 的 & 0xff 写法）。
     */
    private static int murmur1UnsignedTailReference(String data) {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        int m = 0xc6a4a793;
        int r = 16;
        int length = bytes.length;
        int hash = 0 ^ (length * m);
        int length4 = length >> 2;
        for (int i = 0; i < length4; i++) {
            int i4 = i << 2;
            int k = (bytes[i4] & 0xff);
            k |= (bytes[i4 + 1] & 0xff) << 8;
            k |= (bytes[i4 + 2] & 0xff) << 16;
            k |= (bytes[i4 + 3] & 0xff) << 24;
            hash = (hash + k);
            hash = (hash * m);
            hash ^= (hash >> 16);
        }
        int offset = length4 << 2;
        switch (length & 3) {
            case 3:
                hash += ((bytes[offset + 2] & 0xff) << 16);
                // fall through
            case 2:
                hash += ((bytes[offset + 1] & 0xff) << 8);
                // fall through
            case 1:
                hash += (bytes[offset] & 0xff);
                hash = (hash * m);
                hash ^= (hash >> r);
        }
        hash = (hash * m);
        hash ^= (hash >> 10);
        hash = (hash * m);
        hash ^= (hash >> 17);
        return hash;
    }

    @Test
    public void murmur1AsciiResultsUnchanged() {
        assertMurmur1AsciiGolden();
    }

    @Test
    public void murmur1TailFoldsUnsignedLikeReference() {
        String[] samples = {"abcÿ", "ab中", "abc\u0080", "abcÿ", "中", "a😀b", "配置", "abcd", "abcde", "ab"};
        for (String sample : samples) {
            assertEquals(murmur1UnsignedTailReference(sample), HashAide.murmur1StringHash32(sample),
                         "尾字节必须按 0~255 无符号参与折叠：" + sample);
        }
        // 修复前实测的符号扩展偏差值，修复后必须不再出现
        assertNotEquals(0x62d3e917, HashAide.murmur1StringHash32("abcÿ"));
        assertNotEquals(0x08d78119, HashAide.murmur1StringHash32("ab中"));
    }

    @Test
    public void murmur1TailHighByteDifferenceMustShowUp() {
        assertNotEquals(HashAide.murmur1StringHash32("abc\u0080"), HashAide.murmur1StringHash32("abcÿ"),
                        "仅尾字节高位不同的同长度文本哈希必须不同");
    }

    @Test
    public void murmur2RemainsUntouched() {
        assertEquals(0xa2d0b27c, HashAide.murmur2StringHash32("a"));
        assertEquals(0x12d8262a, HashAide.murmur2StringHash32("ab"));
        assertEquals(0x1c94221b, HashAide.murmur2StringHash32("abc"));
        assertEquals(0xb11ab5f4, HashAide.murmur2StringHash32("abcd"));
    }

    // ==================== 6. ObjectAide 类型令牌解析 ====================

    @SuppressWarnings("unused")
    private static java.util.List<?> wildcardHolder;

    /**
     * 捕获实例同时实现一个无关接口：接口列表非空且首位不是 ParameterizedType（修复前错层取型直接异常）。
     */
    public static class StringTokenWithInterface extends ReferenceType<String> implements Runnable {
        @Override
        public void run() {
        }
    }

    private static Type wildcardType() throws Exception {
        Field field = NumericHashIntegrityContractTest.class.getDeclaredField("wildcardHolder");
        return ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
    }

    @Test
    public void capturedReferenceTypeConvertsToDeclaredClass() {
        String same = "abc";
        assertSame(same, ObjectAide.convertTo(same, new ReferenceType<String>() {
        }), "已是目标类型的实例原样通过（同一引用）");
        assertEquals(Integer.valueOf(123), ObjectAide.convertTo("123", new ReferenceType<Integer>() {
        }));
        assertEquals(Long.valueOf(123), ObjectAide.convertTo("123", new ReferenceType<Long>() {
        }));
    }

    @Test
    public void captureWithUnrelatedInterfaceStillResolvesDirectSuperclass() {
        StringTokenWithInterface token = new StringTokenWithInterface();
        assertEquals("abc", ObjectAide.convertTo("abc", token), "接口列表不得参与取型，直接父层实参才是唯一来源");
    }

    @Test
    public void genericInterfaceCaptureResolvesRawType() {
        java.util.List<String> converted = ObjectAide.convertTo(java.util.Arrays.asList("x"),
                                                                new ReferenceType<java.util.List<String>>() {
                                                                });
        assertEquals(1, converted.size());
    }

    @Test
    public void forTypeTokenResolvesConcreteType() {
        assertEquals(Integer.valueOf(123), ObjectAide.convertTo("123", ReferenceType.forType(Integer.class)));
        assertEquals("123", ObjectAide.convertTo(123, ReferenceType.forType(String.class)));
    }

    @Test
    public void unresolvableWildcardFailsExplicitly() throws Exception {
        ReferenceType<Object> token = ReferenceType.forType(wildcardType());
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                                                       () -> ObjectAide.convertTo("x", token));
        assertSame(IllegalArgumentException.class, thrown.getClass(), "必须受控校验失败，不得下标越界或静默错型");
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void rawCaptureFailsExplicitlyAtConstruction() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            Object unused = new ReferenceType() {
            };
        });
        assertTrue(thrown.getMessage().contains("parameterized"), "原始捕获当场受控失败");
    }

    @Test
    public void unconvertibleValueRejectedExplicitly() {
        assertThrows(ClassCastException.class, () -> ObjectAide.convertTo(new Object() {
        }, new ReferenceType<Integer>() {
        }));
    }

}
