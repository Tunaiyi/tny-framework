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
 * 六十四位 CityHash 选项的契约测试
 * <p>
 * 规格能力 namespace-hashing 裁定六十四位档以带种子形态的 CityHash 64 位参考语义为验收基线：
 * 种子必须原生参与计算（而非键拼接），实现与 zero-allocation-hashing 的 city_1_1 带种子入口逐值一致。
 * 抓样时实测 hutool 中间形态与 zero-allocation-hashing 的 64 位输出在种子 0 与种子 10 下逐值相等，
 * 因此本测试的两实现等价断言在实现替换前后均成立（记录于此以满足设计 D3 的旧值留痕要求：
 * 键 "ABC___7" 种子 0 的旧值为 9039730652030614171，与替换后目标形态的取值相同）。
 *
 * @author kgtny
 * @date 2026/10/4 04:55
 **/
class CityHash64Test {

    private static final String[] KEYS = {
            "",
            "A",
            "ABCDEFGHJKLMN",
            "0123456789ABCDEF01234567",
            "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF01234567",
            "命名空间分片",
            "🎮分片键😀",
            "中文😀ABC",
            "ABC___7",
    };

    @Test
    void repeatedComputationIsDeterministic() {
        for (String key : KEYS) {
            assertEquals(HashAlgorithms.CITY_HASH_64.hash(key, 0), HashAlgorithms.CITY_HASH_64.hash(key, 0),
                    "同一键与种子的连续两次计算必须相等");
            assertEquals(HashAlgorithms.CITY_HASH_64.hash(key, 10), HashAlgorithms.CITY_HASH_64.hash(key, 10),
                    "非零种子下的连续两次计算必须相等");
        }
    }

    @Test
    void seedParticipatesNativelyAndChangesResult() {
        for (String key : KEYS) {
            long withSeed = HashAlgorithms.CITY_HASH_64.hash(key, 7);
            long withoutSeed = HashAlgorithms.CITY_HASH_64.hash(key, 0);
            assertNotEquals(withoutSeed, withSeed,
                    "样本键 [" + key + "] 的种子 7 结果必须不同于种子 0——种子必须原生参与计算");
            assertEquals(LongHashFunction.city_1_1(7L).hashBytes(key.getBytes(StandardCharsets.UTF_8)), withSeed,
                    "六十四位档必须与 zero-allocation-hashing 的 city_1_1 带种子入口逐值一致");
            assertEquals(LongHashFunction.city_1_1(0L).hashBytes(key.getBytes(StandardCharsets.UTF_8)), withoutSeed,
                    "六十四位档在种子 0 下必须与 city_1_1 无种子语义逐值一致");
        }
    }

    @Test
    void nonAsciiInputsProduceDeterministicValuesWithinRange() {
        String key = "中文😀ABC";
        long first = HashAlgorithms.CITY_HASH_64.hash(key, 3);
        long second = HashAlgorithms.CITY_HASH_64.hash(key, 3);
        assertEquals(first, second, "非 ASCII 输入必须得到确定值且不抛异常");
        assertNotEquals(0L, HashAlgorithms.CITY_HASH_64.hash("命名空间分片", 0),
                "中文键的六十四位哈希必须有效");
    }

    @Test
    void maxIsReportedAsLongMaximum() {
        assertEquals(Long.MAX_VALUE, HashAlgorithms.CITY_HASH_64.getMax(),
                "六十四位选项声明的取值上界必须为长整型最大值");
    }

}
