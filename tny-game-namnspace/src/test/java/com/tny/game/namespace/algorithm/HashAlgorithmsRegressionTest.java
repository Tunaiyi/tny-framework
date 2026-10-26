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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 算法集回归护栏测试
 * <p>
 * 实现替换类变更（如移除某个底层算法库依赖）不得改变 CityHash 两档之外任何算法选项的
 * 哈希结果，也不得改变缺省算法。本类的样本值在 CityHash 实现替换之前抓取固化，
 * 用作长期回归基线（规格能力 namespace-hashing 的"其他选项与缺省选项不受扰动"要求）。
 *
 * @author kgtny
 * @date 2026/10/4 05:00
 **/
class HashAlgorithmsRegressionTest {

    private static final String SAMPLE_KEY = "ABC___7";

    @Test
    void defaultAlgorithmRemainsXxh3Hash32() {
        assertSame(HashAlgorithms.XXH3_HASH_32, HashAlgorithms.getDefault(),
                "缺省算法必须仍为 XXH3_HASH_32");
    }

    @Test
    void otherAlgorithmsSampleValuesUnchanged() {
        assertEquals(149994371L, HashAlgorithms.MURMUR3_32.hash(SAMPLE_KEY, 0),
                "MURMUR3_32 对样本键的返回值必须不变");
        assertEquals(644220921542709928L, HashAlgorithms.MURMUR3_64.hash(SAMPLE_KEY, 0),
                "MURMUR3_64 对样本键的返回值必须不变");
        assertEquals(3681344893L, HashAlgorithms.XX_HASH_32.hash(SAMPLE_KEY, 0),
                "XX_HASH_32 对样本键的返回值必须不变");
        assertEquals(-3672080294058890034L, HashAlgorithms.XX_HASH_64.hash(SAMPLE_KEY, 0),
                "XX_HASH_64 对样本键的返回值必须不变");
        assertEquals(2323523971L, HashAlgorithms.XXH3_HASH_32.hash(SAMPLE_KEY, 0),
                "XXH3_HASH_32 对样本键的返回值必须不变");
        assertEquals(-8467284604611524860L, HashAlgorithms.XXH3_HASH_64.hash(SAMPLE_KEY, 0),
                "XXH3_HASH_64 对样本键的返回值必须不变");
        assertEquals(9039730652030614171L, HashAlgorithms.FARM_HASH_32.hash(SAMPLE_KEY, 0),
                "FARM_HASH_32 对样本键的返回值必须不变");
        assertEquals(9039730652030614171L, HashAlgorithms.FARM_HASH_64.hash(SAMPLE_KEY, 0),
                "FARM_HASH_64 对样本键的返回值必须不变");
        assertEquals(2738600740L, HashAlgorithms.METRO_HASH_32.hash(SAMPLE_KEY, 0),
                "METRO_HASH_32 对样本键的返回值必须不变");
        assertEquals(-6684543454631991762L, HashAlgorithms.METRO_HASH_64.hash(SAMPLE_KEY, 0),
                "METRO_HASH_64 对样本键的返回值必须不变");
    }

}
