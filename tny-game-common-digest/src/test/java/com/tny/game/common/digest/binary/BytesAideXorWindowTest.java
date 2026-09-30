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

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * net-protocol 规格"载荷加密的全窗口覆盖与相对键流相位"的单元契约（fix-xor-crypto-scope 1.1）。
 * 复刻生产调用形态（XOrCodecCrypto：security 与 4 字节 code 双键流、池化堆缓冲非零 arrayOffset）。
 * 期望语义：处理完整窗口 [offset, offset+length)，键流相位 = (i - offset) % keys.length。
 */
class BytesAideXorWindowTest {

    private static final byte[] SECURITY = new byte[]{
            (byte) 0xA1, (byte) 0xB2, (byte) 0xC3, (byte) 0xD4,
            (byte) 0xE5, (byte) 0x06, (byte) 0x17, (byte) 0x28};
    private static final byte[] CODE = new byte[]{0x31, (byte) 0x7A, (byte) 0x0C, (byte) 0x5B};

    /** 期望键流：S[rel] = SECURITY[rel % 8] ^ CODE[rel % 4]（全窗、相对相位） */
    private static byte[] expectedStream(int length) {
        byte[] s = new byte[length];
        for (int rel = 0; rel < length; rel++) {
            s[rel] = (byte) (SECURITY[rel % SECURITY.length] ^ CODE[rel % CODE.length]);
        }
        return s;
    }

    /** (a) 全窗覆盖：窗口 [2, 12) 的每个字节都必须被变换，窗口外字节不受触碰 */
    @Test
    void entireWindowIsTransformed() {
        int offset = 2;
        int length = 10;
        byte[] data = new byte[24];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (i + 1);
        }
        byte[] snapshot = data.clone();
        byte[] stream = expectedStream(length);

        BytesAide.xor(data, offset, length, SECURITY, CODE);

        for (int rel = 0; rel < length; rel++) {
            assertEquals((byte) (snapshot[offset + rel] ^ stream[rel]), data[offset + rel],
                    "窗口内第 " + rel + " 个字节未被变换（或键流相位错误）");
        }
        for (int outside = 0; outside < offset; outside++) {
            assertEquals(snapshot[outside], data[outside], "窗口前字节不应被处理");
        }
        for (int outside = offset + length; outside < data.length; outside++) {
            assertEquals(snapshot[outside], data[outside], "窗口后字节不应被处理");
        }
    }

    /** (b) 相位无关：同一消息在窗口起点 0 与非 0 的底层数组观下，变换结果逐字节相同 */
    @Test
    void keystreamPhaseIsIndependentOfBufferPosition() {
        int length = 10;
        byte[] message = new byte[length];
        new Random(11).nextBytes(message);

        byte[] atZero = Arrays.copyOf(message, length);
        BytesAide.xor(atZero, 0, length, SECURITY, CODE);

        byte[] page = new byte[length + 13];
        System.arraycopy(message, 0, page, 13, length);
        BytesAide.xor(page, 13, length, SECURITY, CODE);

        assertArrayEquals(atZero, Arrays.copyOfRange(page, 13, 13 + length),
                "同一消息因缓冲池位置不同产生了不同密文——键流相位绑定了绝对下标");
    }

    /** (c) 自逆往返：同参数执行两次还原原文 */
    @Test
    void xorTwiceRestoresOriginal() {
        int offset = 5;
        int length = 17;
        byte[] data = new byte[32];
        new Random(7).nextBytes(data);
        byte[] original = data.clone();

        BytesAide.xor(data, offset, length, SECURITY, CODE);
        BytesAide.xor(data, offset, length, SECURITY, CODE);

        assertArrayEquals(original, data, "加密两次应还原（XOR 自逆契约）");
    }

    /** (d) 兼容锚：offset=0 时行为与历史实现（绝对=相对下标重合）逐字节一致 */
    @Test
    void zeroOffsetKeepsHistoricalSemantics() {
        int length = 12;
        byte[] data = new byte[length];
        new Random(3).nextBytes(data);
        byte[] stream = expectedStream(length);
        byte[] expected = data.clone();
        for (int rel = 0; rel < length; rel++) {
            expected[rel] = (byte) (expected[rel] ^ stream[rel]);
        }

        BytesAide.xor(data, 0, length, SECURITY, CODE);

        assertArrayEquals(expected, data, "offset=0 的既有语义不得变化");
    }
}
