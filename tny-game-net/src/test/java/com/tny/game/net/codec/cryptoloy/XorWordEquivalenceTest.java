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
package com.tny.game.net.codec.cryptoloy;

import com.tny.game.common.digest.binary.*;
import com.tny.game.net.codec.*;
import org.junit.jupiter.api.*;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * optimize-legacy-codec-paths 1.1：XOr 字级化改造的等价护栏（改动前锁定基线）。
 * oracle = {@link BytesAide#xor(byte[], int, int, byte[]...)}（永久参考实现，设计决策 1）；
 * 键长集合覆盖 lcm(secLen,4) 非 8 对齐周期；断言：逐字节等值 + 自逆 + 窗外零触碰。
 * 与 XorTileCodecCrypto 既有测试构成三方传递锁定（字级 XOr ≡ oracle ≡ xorTile）。
 */
class XorWordEquivalenceTest {

    private static final String[] KEY_LENGTHS = {"k2", "1234567", "bench-key", "sixteen-chars-k!"};

    private static DataPackCodecOptions options(String key) {
        DataPackCodecOptions config = new DataPackCodecOptions();
        config.setSecurityKeys(new String[]{key});
        return config;
    }

    /**
     * 穷举扫描（对抗评审产出，将一次性证明固化为常驻回归）：键长 1..64 全量 + {72,80,128}，
     * 交错次序强制 holder 数组增长-缩回复用；offset 0..6 × len 0..140 步长 2 × 两个 code 值；
     * 覆盖 aligned==64 阈值恰边界、wordCount=3（demo 键长 12/14）、计数器路径多周期形态、
     * 字级/尾部衔界多回卷（len ≥ 2×aligned）。
     */
    @Test
    void exhaustiveSweepMatchesOracle() {
        java.util.List<Integer> lengths = new java.util.ArrayList<>();
        for (int i = 1; i <= 64; i++) {
            lengths.add(i);
        }
        lengths.add(128);
        lengths.add(72);
        lengths.add(80);
        java.util.Collections.sort(lengths, (a, b) -> Integer.compare(Integer.rotateLeft(a, 13), Integer.rotateLeft(b, 13)));

        XOrCodecCrypto crypto = new XOrCodecCrypto();
        for (int sLen : lengths) {
            char[] chars = new char[sLen];
            java.util.Arrays.fill(chars, (char) ('A' + (sLen % 26)));
            for (int ci = 0; ci < 2; ci++) {
                DataPackCodecOptions config = new DataPackCodecOptions();
                config.setSecurityKeys(new String[]{new String(chars)});
                DataPackageContext packager = new DataPackageContext(1777L + sLen, config);
                packager.nextNumber();
                if (ci == 1) {
                    for (int w = 0; w < 3; w++) {
                        packager.nextNumber();
                    }
                }
                byte[] sec = packager.getPackSecurityKey();
                byte[] code = BytesAide.int2Bytes(packager.getPacketCode());
                for (int offset = 0; offset <= 6; offset++) {
                    for (int len = 0; len <= 140; len += 2) {
                        byte[] pageA = new byte[offset + len + 4];
                        byte[] pageB = pageA.clone();
                        java.util.Random fill = new java.util.Random(sLen * 31 + offset * 7 + len);
                        fill.nextBytes(pageA);
                        System.arraycopy(pageA, 0, pageB, 0, pageA.length);
                        byte[] original = pageA.clone();

                        crypto.encrypt(packager, pageA, offset, len);
                        BytesAide.xor(pageB, offset, len, sec, code);
                        assertArrayEquals(pageB, pageA,
                                "穷举漂移 sLen=" + sLen + " offset=" + offset + " len=" + len);
                        for (int i = 0; i < offset; i++) {
                            assertEquals(original[i], pageA[i], "窗前越界 sLen=" + sLen);
                        }
                        for (int i = offset + len; i < pageA.length; i++) {
                            assertEquals(original[i], pageA[i], "窗后越界 sLen=" + sLen);
                        }
                    }
                }
            }
        }
    }

    @Test
    void xorEqualsReferenceOracleAcrossWindowsAndKeyLengths() {
        XOrCodecCrypto crypto = new XOrCodecCrypto();
        for (String key : KEY_LENGTHS) {
            Random random = new Random(key.hashCode() & 0x7fffffff);
            for (int round = 0; round < 200; round++) {
                int len = 1 + random.nextInt(96);
                byte[] message = new byte[len];
                random.nextBytes(message);
                int offset = random.nextInt(24);

                byte[] pageX = new byte[offset + len + 3];
                System.arraycopy(message, 0, pageX, offset, len);
                byte[] original = pageX.clone();          // 明文页（自逆比较对象）
                byte[] pageR = pageX.clone();

                DataPackageContext packager = new DataPackageContext(31337L + round, options(key));
                packager.nextNumber();
                byte[] sec = packager.getPackSecurityKey();
                byte[] code = BytesAide.int2Bytes(packager.getPacketCode());

                crypto.encrypt(packager, pageX, offset, len);
                BytesAide.xor(pageR, offset, len, sec, code);   // oracle：生产参考工具

                assertArrayEquals(pageR, pageX,
                        "XOr 与 oracle 不等价（key=" + key + " offset=" + offset + " len=" + len + "）");
                for (int i = offset + len; i < pageX.length; i++) {
                    assertEquals(0, pageX[i], "窗口后字节被越界触碰");
                }

                byte[] restored = pageX.clone();
                crypto.decrypt(packager, restored, offset, len);
                assertArrayEquals(original, restored, "自逆还原失败");
            }
        }
    }
}
