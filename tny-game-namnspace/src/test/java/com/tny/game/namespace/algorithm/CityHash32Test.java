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
package com.tny.game.namespace.algorithm;

import net.openhft.hashing.LongHashFunction;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 三十二位 CityHash 选项的契约测试
 * <p>
 * 黄金向量于实现替换之前从当时的 hutool 形态（其本身是 Google CityHash 参考实现的移植）
 * 抓取固化，覆盖长度分支各段与中文、表情符号输入；本测试同时钉住取值范围、
 * 确定性、上界报告与键拼接式种子参与约定（规格能力 namespace-hashing）。
 *
 * @author kgtny
 * @date 2026/10/4 04:50
 **/
class CityHash32Test {

    private static final long MAX_UNSIGNED_32 = 4294967295L;

    /** 样本键集，按 UTF-8 字节长度覆盖 0、1 到 4、5 到 12、13 到 24、25 以上各长度分支段。 */
    private static final String[] KEYS = {
            "",
            "A",
            "AB",
            "ABC",
            "ABCD",
            "ABCDE",
            "ABCDEFG",
            "ABCDEFGHI",
            "ABCDEFGHJKLM",
            "ABCDEFGHJKLMN",
            "ABCDEFGHIJKLMNOP",
            "ABCDEFGHIJKLMNOPQRSTUVWX",
            "abcdefghijklmnopqrstuvwxy",
            "0123456789ABCDEF01234567",
            "0123456789ABCDEF0123456789ABCDEFG",
            "0123456789ABCDEF012345678901234567",
            "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF01234567",
            "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF01234567",
            "命名空间分片",
            "🎮分片键😀",
            "中文😀ABC",
            "ABC___7",
    };

    /** 种子 0 的黄金向量，替换前从 hutool 形态抓取固化（对应 Google city.cpp 的 CityHash32 函数）。 */
    private static final long[] GOLDEN_SEED_0 = {
            3696677242L,
            1075162589L,
            4274275221L,
            381166867L,
            2804865809L,
            1422643275L,
            1485702400L,
            3513013225L,
            2210870879L,
            431235769L,
            1987109900L,
            3419369868L,
            3562860892L,
            437090011L,
            4061056267L,
            1308744878L,
            3348541487L,
            2986948249L,
            2669341787L,
            3536506564L,
            1303775060L,
            85559553L,
    };

    /** 种子 7 的黄金向量（键拼接约定下的结果），与 GOLDEN_SEED_0 同序。 */
    private static final long[] GOLDEN_SEED_7 = {
            2762651575L,
            1324874750L,
            3697129217L,
            106053140L,
            24601999L,
            730469267L,
            3580909778L,
            2113576562L,
            3918299432L,
            3386309262L,
            4183672502L,
            2201665649L,
            76418890L,
            866892391L,
            70554267L,
            554256579L,
            49004136L,
            3154611841L,
            2796287855L,
            1599574708L,
            1194190286L,
            3441202411L,
    };

    @Test
    void portClassOutputsMatchGoldenVectors() {
        for (int i = 0; i < KEYS.length; i++) {
            long value = CityHash32.hashUnsigned(KEYS[i].getBytes(StandardCharsets.UTF_8));
            assertEquals(GOLDEN_SEED_0[i], value,
                    "移植类对样本键 [" + KEYS[i] + "] 的输出必须与黄金向量逐值相等");
        }
    }

    @Test
    void goldenVectorsMatchSeedZeroOutputs() {
        for (int i = 0; i < KEYS.length; i++) {
            long value = HashAlgorithms.CITY_HASH_32.hash(KEYS[i], 0);
            assertEquals(GOLDEN_SEED_0[i], value,
                    "样本键 [" + KEYS[i] + "] 的三十二位 CityHash 结果必须与固化的黄金向量相等");
        }
    }

    @Test
    void outputsStayInUnsignedThirtyTwoBitRangeAndMaxIsReported() {
        for (String key : KEYS) {
            long value = HashAlgorithms.CITY_HASH_32.hash(key, 0);
            assertTrue(value >= 0 && value <= MAX_UNSIGNED_32,
                    "样本键 [" + key + "] 的返回值必须落在无符号三十二位区间");
        }
        assertEquals(MAX_UNSIGNED_32, HashAlgorithms.CITY_HASH_32.getMax(),
                "三十二位选项声明的取值上界必须为 4294967295");
    }

    @Test
    void byteLevelDifferentChineseKeysDiffer() {
        long a = HashAlgorithms.CITY_HASH_32.hash("命名空间分片", 0);
        long b = HashAlgorithms.CITY_HASH_32.hash("命名空间分片啊", 0);
        assertNotEquals(a, b, "两个仅字节内容相差一个字符的中文文本必须返回不同的哈希值");
    }

    @Test
    void repeatedComputationIsDeterministic() {
        for (String key : KEYS) {
            assertEquals(HashAlgorithms.CITY_HASH_32.hash(key, 0), HashAlgorithms.CITY_HASH_32.hash(key, 0),
                    "同一键与种子的连续两次计算必须相等");
        }
    }

    @Test
    void seedParticipatesThroughKeyConcatenation() {
        for (int i = 0; i < KEYS.length; i++) {
            String key = KEYS[i];
            assertEquals(GOLDEN_SEED_7[i], HashAlgorithms.CITY_HASH_32.hash(key, 7),
                    "样本键 [" + key + "] 在种子 7 下的结果必须与固化的键拼接黄金向量相等");
            assertEquals(HashAlgorithms.CITY_HASH_32.hash(key + "7", 0), HashAlgorithms.CITY_HASH_32.hash(key, 7),
                    "种子必须表现为把种子文本拼接到键之后再计算");
        }
        assertTrue(HashAlgorithms.CITY_HASH_32.hash("ABC", 7) != HashAlgorithms.CITY_HASH_32.hash("ABC", 0),
                "非零种子必须改变哈希结果");
    }

    @Test
    void outputIsNotFoldedFromSixtyFourBitCityHash() {
        int differentFromLow = 0;
        int differentFromHigh = 0;
        for (String key : KEYS) {
            long city64 = LongHashFunction.city_1_1(0L).hashBytes(key.getBytes(StandardCharsets.UTF_8));
            long value = HashAlgorithms.CITY_HASH_32.hash(key, 0);
            if (value != (city64 & MAX_UNSIGNED_32)) {
                differentFromLow++;
            }
            if (value != (city64 >>> 32)) {
                differentFromHigh++;
            }
        }
        assertTrue(differentFromLow > 0, "三十二位档的输出不得是六十四位 CityHash 结果的低三十二位截断");
        assertTrue(differentFromHigh > 0, "三十二位档的输出不得是六十四位 CityHash 结果的高三十二位截断");
    }

}
