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
package com.tny.game.common.digest.binary;

import org.junit.jupiter.api.*;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BytesAide 数值↔字节编解码契约（网络层生产依赖：XOrCodecCrypto 用 int2Bytes 生成包码键流、
 * CRC64CodecVerifier 用 int2Bytes/long2Bytes 序列化校验块、CodecLogger 用 hex/binary 打印）。
 * 钉死：小端字节序、往返一致性、短窗口（1~7 字节）读取、无符号扩展。
 */
class BytesAideCodecTest {

    /** 生产形态：包码 int2Bytes 必须为小端（0x04030201 → 01 02 03 04），XOR 键流相位以此为前提 */
    @Test
    void int2BytesIsLittleEndian() {
        assertArrayEquals(new byte[]{0x01, 0x02, 0x03, 0x04}, BytesAide.int2Bytes(0x04030201));
        byte[] target = new byte[6];
        BytesAide.int2Bytes(0x04030201, target, 2);
        assertArrayEquals(new byte[]{0, 0, 0x01, 0x02, 0x03, 0x04}, target);
    }

    @Test
    void long2BytesIsLittleEndian() {
        assertArrayEquals(new byte[]{0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08},
                BytesAide.long2Bytes(0x0807060504030201L));
        byte[] target = new byte[10];
        BytesAide.long2Bytes(0x0807060504030201L, target, 2);
        assertArrayEquals(new byte[]{0, 0, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08}, target);
    }

    @Test
    void intRoundTrip() {
        int[] samples = {0, 1, -1, Integer.MAX_VALUE, Integer.MIN_VALUE, 0x00FF00FF, 0xFF00FF00};
        for (int value : samples) {
            assertEquals(value, BytesAide.bytes2Int(BytesAide.int2Bytes(value)), "int 往返丢失: " + value);
        }
        // 种子固定的随机批量往返
        Random random = new Random(20260930L);
        for (int i = 0; i < 1000; i++) {
            int value = random.nextInt();
            assertEquals(value, BytesAide.bytes2Int(BytesAide.int2Bytes(value)));
        }
    }

    @Test
    void longRoundTrip() {
        long[] samples = {0L, 1L, -1L, Long.MAX_VALUE, Long.MIN_VALUE, 0x00FF00FF00FF00FFL};
        for (long value : samples) {
            assertEquals(value, BytesAide.bytes2Long(BytesAide.long2Bytes(value)), "long 往返丢失: " + value);
        }
        Random random = new Random(20260930L);
        for (int i = 0; i < 1000; i++) {
            long value = random.nextLong();
            assertEquals(value, BytesAide.bytes2Long(BytesAide.long2Bytes(value)));
        }
    }

    /** 短窗口 bytes2Long：switch 覆盖 1~7 字节剩余长度（fix：7 字节原落 default 分支读 8 字节越界） */
    @Test
    void bytes2LongHandlesShortWindows() {
        byte[] data = {0x08, 0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01};
        // 逐长度断言：data[startAt] 为最低字节（小端），高位缺失部分补 0
        assertEquals(0x01L, BytesAide.bytes2Long(data, 7));
        assertEquals(0x0102L, BytesAide.bytes2Long(data, 6));
        assertEquals(0x010203L, BytesAide.bytes2Long(data, 5));
        assertEquals(0x01020304L, BytesAide.bytes2Long(data, 4));
        assertEquals(0x0102030405L, BytesAide.bytes2Long(data, 3));
        assertEquals(0x010203040506L, BytesAide.bytes2Long(data, 2));
        assertEquals(0x01020304050607L, BytesAide.bytes2Long(data, 1)); // 剩余 7 字节，曾越界
        assertEquals(0x0102030405060708L, BytesAide.bytes2Long(data, 0));
    }

    /** bytes2Int 的 1~3 字节短窗口（default 读满 4 字节，要求数组至少 4 长，短数组走 case） */
    @Test
    void bytes2IntShortWindows() {
        assertEquals(0x01, BytesAide.bytes2Int(new byte[]{0x01}));
        assertEquals(0x0201, BytesAide.bytes2Int(new byte[]{0x01, 0x02}));
        assertEquals(0x030201, BytesAide.bytes2Int(new byte[]{0x01, 0x02, 0x03}));
        assertEquals(0x04030201, BytesAide.bytes2Int(new byte[]{0x01, 0x02, 0x03, 0x04}));
    }

    @Test
    void shortRoundTrip() {
        short[] samples = {0, 1, -1, Short.MAX_VALUE, Short.MIN_VALUE, (short) 0x0102};
        for (short value : samples) {
            assertEquals(value, BytesAide.bytes2Short(BytesAide.short2Bytes(value)), "short 往返丢失: " + value);
        }
        assertArrayEquals(new byte[]{0x02, 0x01}, BytesAide.short2Bytes((short) 0x0102));
    }

    /**
     * 浮点/双精度往返：常规值与极值按位精确还原，特殊位形（NaN、±Infinity、-0.0）保型。
     * 断言用位形比较（floatToIntBits/doubleToLongBits）而非只有数值比较——数值比较分不开
     * 被折算成 0.0 的 -0.0，也无法证明无穷位形没被改写；网络层键流与校验码走的是字节，位形即契约。
     */
    @Test
    void floatDoubleRoundTrip() {
        for (float f : new float[]{0f, -0.0f, 1.5f, -1.5f, Float.MAX_VALUE, Float.MIN_VALUE, Float.NaN,
                Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY}) {
            float back = BytesAide.bytes2Float(BytesAide.float2Bytes(f));
            if (Float.isNaN(f)) {
                assertTrue(Float.isNaN(back), "NaN 未保型");
                assertEquals(Float.floatToIntBits(Float.NaN), Float.floatToIntBits(back), "NaN 位形被改写");
            } else {
                assertEquals(f, back, 0.0f);
                assertEquals(Float.floatToIntBits(f), Float.floatToIntBits(back), "float 位形丢失: " + f);
            }
        }
        for (double d : new double[]{0d, -0.0d, 3.141592653589793d, -2.71828d, Double.MAX_VALUE, Double.MIN_VALUE,
                Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            double back = BytesAide.bytes2Double(BytesAide.double2Bytes(d));
            if (Double.isNaN(d)) {
                assertTrue(Double.isNaN(back), "double NaN 未保型");
                assertEquals(Double.doubleToLongBits(Double.NaN), Double.doubleToLongBits(back), "double NaN 位形被改写");
            } else {
                assertEquals(d, back, 0d);
                assertEquals(Double.doubleToLongBits(d), Double.doubleToLongBits(back), "double 位形丢失: " + d);
            }
        }
        // 正负无穷必须是"仍是无穷且符号不变"（数值相等不足以证明位形保真）
        for (double infinity : new double[]{Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            double back = BytesAide.bytes2Double(BytesAide.double2Bytes(infinity));
            assertTrue(Double.isInfinite(back), "无穷位形被折算成普通数值: " + infinity);
            assertEquals(infinity > 0, back > 0, "无穷符号丢失: " + infinity);
        }
        // 符号位独立保真：-0.0 与 0.0 数值相等，序列化必须各自还原符号
        assertEquals(0x8000000000000000L, Double.doubleToRawLongBits(
                BytesAide.bytes2Double(BytesAide.double2Bytes(-0.0d))), "-0.0 符号位丢失");
        assertEquals(0x80000000, Float.floatToRawIntBits(
                BytesAide.bytes2Float(BytesAide.float2Bytes(-0.0f))), "-0.0f 符号位丢失");
        // 带尾数载荷的 NaN 在编码期被归一为规范 NaN（doubleToLongBits 的 JLS 归一语义）：
        // 往返只保证"仍是 NaN 且位形规范"，不保证尾数载荷逐位保真——按现状钉桩
        double payloadNaN = Double.longBitsToDouble(0x7ff8_0000_0000_0011L);
        assertEquals(0x7ff8_0000_0000_0011L, Double.doubleToRawLongBits(payloadNaN), "样本必须是带尾数的 NaN");
        double normalized = BytesAide.bytes2Double(BytesAide.double2Bytes(payloadNaN));
        assertTrue(Double.isNaN(normalized), "带尾数的 NaN 往返后不再是 NaN");
        assertEquals(Double.doubleToLongBits(Double.NaN), Double.doubleToLongBits(normalized), "NaN 未归一为规范位形");
    }

    /** 无符号扩展：任意 byte（含负数）都必须落在 0~255（fix：原掩码 0xfff 把 -1 解成 4095） */
    @Test
    void bytes2UnsignedIntIsByteWide() {
        for (int i = 0; i < 256; i++) {
            byte b = (byte) i;
            int unsigned = BytesAide.bytes2UnsignedInt(b);
            assertEquals(i & 0xFF, unsigned, "byte " + b + " 无符号解析错误");
            assertTrue(unsigned >= 0 && unsigned <= 255);
        }
        assertEquals(255, BytesAide.bytes2UnsignedInt((byte) -1));
    }

    /** 生产形态一致性：xor(data, keys) 全数组入口必须等价于窗口入口 [0, length) */
    @Test
    void xorFullArrayEntryEqualsWindowEntry() {
        byte[] key = {0x5A, (byte) 0xC3, 0x11, 0x77, (byte) 0xE9};
        byte[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        byte[] b = a.clone();
        assertArrayEquals(BytesAide.xor(a, 0, a.length, key), BytesAide.xor(b, key));
    }

    @Test
    void binaryAndHexRendering() {
        assertEquals("10000000", BytesAide.toBinaryString(Byte.MIN_VALUE));
        assertEquals("01111111", BytesAide.toBinaryString(Byte.MAX_VALUE));
        assertEquals("00000000", BytesAide.toBinaryString((byte) 0));
        // 多数组 + 分隔符：每个字节 8 位二进制，字节间（含组尾）都追加分隔符
        byte[] msg = {Byte.MIN_VALUE, 0, Byte.MAX_VALUE};
        assertEquals("10000000 00000000 01111111 10000000 00000000 01111111 ",
                BytesAide.toBinaryString(" ", msg, msg));
        assertEquals("80007f", BytesAide.toHexString(msg));
        assertEquals("80007f80007f", BytesAide.toHexString(msg, msg));
        // join 逐个输出有符号十进制字节并以 link 相连
        assertEquals("-128, 0, 127", BytesAide.join(msg, ", "));
    }

}
