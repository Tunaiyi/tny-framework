/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.namespace.consistenthash;

import net.openhft.hashing.LongHashFunction;

import java.util.function.Function;

import static com.tny.game.namespace.NamespaceConstants.*;

/**
 * 哈希算法
 * <p>
 *
 * @author kgtny
 * @date 2022/7/6 13:29
 **/
public class HashAlgorithms {

    public static final HashAlgorithm MURMUR3_32 = algorithm32(LongHashFunction::murmur_3);

    public static final HashAlgorithm MURMUR3_64 = algorithm64(LongHashFunction::murmur_3);

    public static final HashAlgorithm XX3_32_HASH = algorithm32(LongHashFunction::xx3);

    public static final HashAlgorithm XX3_64_HASH = algorithm64(LongHashFunction::xx3);

    public static final HashAlgorithm XX_32_HASH = algorithm32(LongHashFunction::xx);

    public static final HashAlgorithm XX_64_HASH = algorithm64(LongHashFunction::xx);

    public static final HashAlgorithm CITY_32_HASH = algorithm32(LongHashFunction::city_1_1);

    public static final HashAlgorithm CITY_64_HASH = algorithm64(LongHashFunction::city_1_1);

    public static final HashAlgorithm FARM_32_HASH = algorithm32(LongHashFunction::farmUo);

    public static final HashAlgorithm FARM_64_HASH = algorithm64(LongHashFunction::farmUo);

    public static final HashAlgorithm METRO_32_HASH = algorithm32(LongHashFunction::metro);

    public static final HashAlgorithm METRO_64_HASH = algorithm64(LongHashFunction::metro);

    public static final HashAlgorithm WY_32_HASH = algorithm32(LongHashFunction::metro);

    public static final HashAlgorithm WY_64_HASH = algorithm64(LongHashFunction::metro);

    private static final HashAlgorithm DEFAULT = XX3_32_HASH;

    public static HashAlgorithm getDefault() {
        return DEFAULT;
    }

    private static HashAlgorithm algorithm64(Function<Integer, LongHashFunction> hashBuilder) {
        return new Hash64FunctionHashAlgorithm(hashBuilder);
    }

    private static HashAlgorithm algorithm32(Function<Integer, LongHashFunction> hashBuilder) {
        return new Hash32FunctionHashAlgorithm(hashBuilder);
    }

    private static class Hash64FunctionHashAlgorithm implements HashAlgorithm {

        private final Function<Integer, LongHashFunction> hashBuilder;

        private Hash64FunctionHashAlgorithm(Function<Integer, LongHashFunction> hashBuilder) {
            this.hashBuilder = hashBuilder;
        }

        @Override
        public long hash(String key, int seed) {
            return hashBuilder.apply(seed).hashBytes(key.getBytes(CHARSET));
        }

        @Override
        public long getMax() {
            return Long.MAX_VALUE;
        }

    }

    private static class Hash32FunctionHashAlgorithm implements HashAlgorithm {

        public static final long MAX = -1L >>> 32;

        private final Function<Integer, LongHashFunction> hashBuilder;

        private Hash32FunctionHashAlgorithm(Function<Integer, LongHashFunction> hashBuilder) {
            this.hashBuilder = hashBuilder;
        }

        @Override
        public long hash(String key, int seed) {
            long h64 = hashBuilder.apply(seed).hashBytes(key.getBytes(CHARSET));
            return ((h64 >>> 32) | 0xFFFF);
        }

        @Override
        public long getMax() {
            return MAX;
        }

    }

}
