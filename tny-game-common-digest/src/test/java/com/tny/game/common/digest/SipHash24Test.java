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
package com.tny.game.common.digest;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SipHash-2-4 合入门槛测试（add-mac-generation-siphash 1.1）：
 * 官方向量表（veorq/SipHash vectors.h，key=000102...0f，输入为 00 01 ..(n-1) 前缀）逐条断言，
 * 另验流式分段吸收一致性与密钥雪崩。
 */
class SipHash24Test {

    /** 官方 vectors_sip64[64][8]（十进制字节，输出为小端字节序） */
    private static final int[][] OFFICIAL_VECTORS = new int[][]{
        {49, 14, 14, 221, 71, 219, 111, 114},
        {253, 103, 220, 147, 197, 57, 248, 116},
        {90, 79, 169, 217, 9, 128, 108, 13},
        {45, 126, 251, 215, 150, 102, 103, 133},
        {183, 135, 113, 39, 224, 148, 39, 207},
        {141, 166, 153, 205, 100, 85, 118, 24},
        {206, 227, 254, 88, 110, 70, 201, 203},
        {55, 209, 1, 139, 245, 0, 2, 171},
        {98, 36, 147, 154, 121, 245, 245, 147},
        {176, 228, 169, 11, 223, 130, 0, 158},
        {243, 185, 221, 148, 197, 187, 93, 122},
        {167, 173, 107, 34, 70, 47, 179, 244},
        {251, 229, 14, 134, 188, 143, 30, 117},
        {144, 61, 132, 192, 39, 86, 234, 20},
        {238, 242, 122, 142, 144, 202, 35, 247},
        {229, 69, 190, 73, 97, 202, 41, 161},
        {219, 155, 194, 87, 127, 204, 42, 63},
        {148, 71, 190, 44, 245, 233, 154, 105},
        {156, 211, 141, 150, 240, 179, 193, 75},
        {189, 97, 121, 167, 29, 201, 109, 187},
        {152, 238, 162, 26, 242, 92, 214, 190},
        {199, 103, 59, 46, 176, 203, 242, 208},
        {136, 62, 163, 227, 149, 103, 83, 147},
        {200, 206, 92, 205, 140, 3, 12, 168},
        {148, 175, 73, 246, 198, 80, 173, 184},
        {234, 184, 133, 138, 222, 146, 225, 188},
        {243, 21, 187, 91, 184, 53, 216, 23},
        {173, 207, 107, 7, 99, 97, 46, 47},
        {165, 201, 29, 167, 172, 170, 77, 222},
        {113, 101, 149, 135, 102, 80, 162, 166},
        {40, 239, 73, 92, 83, 163, 135, 173},
        {66, 195, 65, 216, 250, 146, 216, 50},
        {206, 124, 242, 114, 47, 81, 39, 113},
        {227, 120, 89, 249, 70, 35, 243, 167},
        {56, 18, 5, 187, 26, 176, 224, 18},
        {174, 151, 161, 15, 212, 52, 224, 21},
        {180, 163, 21, 8, 190, 255, 77, 49},
        {129, 57, 98, 41, 240, 144, 121, 2},
        {77, 12, 244, 158, 229, 212, 220, 202},
        {92, 115, 51, 106, 118, 216, 191, 154},
        {208, 167, 4, 83, 107, 169, 62, 14},
        {146, 89, 88, 252, 214, 66, 12, 173},
        {169, 21, 194, 155, 200, 6, 115, 24},
        {149, 43, 121, 243, 188, 10, 166, 212},
        {242, 29, 242, 228, 29, 69, 53, 249},
        {135, 87, 117, 25, 4, 143, 83, 169},
        {16, 165, 108, 245, 223, 205, 154, 219},
        {235, 117, 9, 92, 205, 152, 108, 208},
        {81, 169, 203, 158, 203, 163, 18, 230},
        {150, 175, 173, 252, 44, 230, 102, 199},
        {114, 254, 82, 151, 90, 67, 100, 238},
        {90, 22, 69, 178, 118, 213, 146, 161},
        {178, 116, 203, 142, 191, 135, 135, 10},
        {111, 155, 180, 32, 61, 231, 179, 129},
        {234, 236, 178, 163, 11, 34, 168, 127},
        {153, 36, 164, 60, 193, 49, 87, 36},
        {189, 131, 141, 58, 175, 191, 141, 183},
        {11, 26, 42, 50, 101, 213, 26, 234},
        {19, 80, 121, 163, 35, 28, 230, 96},
        {147, 43, 40, 70, 228, 215, 6, 102},
        {225, 145, 95, 92, 177, 236, 164, 108},
        {243, 37, 150, 92, 161, 109, 98, 159},
        {87, 95, 242, 142, 96, 56, 27, 229},
        {114, 69, 6, 235, 76, 50, 138, 149}

    };

    private static long le64(byte[] b, int i) {
        return (b[i] & 0xFFL) | ((b[i + 1] & 0xFFL) << 8) | ((b[i + 2] & 0xFFL) << 16) | ((b[i + 3] & 0xFFL) << 24)
                | ((b[i + 4] & 0xFFL) << 32) | ((b[i + 5] & 0xFFL) << 40) | ((b[i + 6] & 0xFFL) << 48) | ((b[i + 7] & 0xFFL) << 56);
    }

    private static byte[] keyBytes() {
        byte[] k = new byte[16];
        for (int i = 0; i < 16; i++) {
            k[i] = (byte) i;
        }
        return k;
    }

    /** (a) 官方 64 向量逐条断言：输入为字节 00..(n-1) */
    @Test
    void matchesOfficialVectorTable() {
        byte[] k = keyBytes();
        byte[] input = new byte[64];
        for (int i = 0; i < 64; i++) {
            input[i] = (byte) i;
        }
        SipHash24 mac = new SipHash24(le64(k, 0), le64(k, 8));
        byte[] out = new byte[8];
        for (int n = 0; n < 64; n++) {
            mac.reset(le64(k, 0), le64(k, 8));
            mac.update(input, 0, n);
            mac.digestBytes(out, 0);
            for (int b = 0; b < 8; b++) {
                assertEquals(OFFICIAL_VECTORS[n][b], out[b] & 0xFF, "vector[" + n + "] byte " + b);
            }
        }
    }

    /** (b) 流式契约：任意分段吸收与一次性吸收全等；reset 复用稳定 */
    @Test
    void streamingEqualsMonolithic() {
        byte[] k = keyBytes();
        byte[] data = new byte[137];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i * 7 + 3);
        }
        SipHash24 one = new SipHash24(le64(k, 0), le64(k, 8));
        one.update(data, 0, data.length);
        long whole = one.digest();

        SipHash24 split = new SipHash24(le64(k, 0), le64(k, 8));
        int off = 0;
        for (int part : new int[]{1, 7, 8, 13, 1, 64, 42}) {
            split.update(data, off, part);
            off += part;
        }
        split.update(data, off, data.length - off);
        assertEquals(whole, split.digest(), "分段吸收必须与整体吸收全等");

        one.reset(le64(k, 0), le64(k, 8));
        one.update(data, 0, data.length);
        assertEquals(whole, one.digest(), "reset 复用结果必须稳定");
    }

    /** (c) 密钥雪崩：1 bit 密钥差 → 输出必须不同 */
    @Test
    void keyAvalanche() {
        byte[] k = keyBytes();
        SipHash24 a = new SipHash24(le64(k, 0), le64(k, 8));
        a.update(new byte[]{1, 2, 3}, 0, 3);
        long va = a.digest();
        SipHash24 b = new SipHash24(le64(k, 0) ^ 1L, le64(k, 8));
        b.update(new byte[]{1, 2, 3}, 0, 3);
        assertNotEquals(va, b.digest(), "1-bit 密钥差必须改变输出");
    }
}
