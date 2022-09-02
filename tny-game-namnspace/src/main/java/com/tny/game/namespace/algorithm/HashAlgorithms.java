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
package com.tny.game.namespace.algorithm;

import cn.hutool.core.util.HashUtil;
import com.google.common.hash.HashFunction;
import net.openhft.hashing.LongHashFunction;
import org.apache.commons.codec.digest.XXHash32;

import java.util.function.*;
import java.util.zip.Checksum;

import static com.tny.game.namespace.NamespaceConstants.*;

/**
 * 哈希算法
 * <p>
 *
 * @author kgtny
 * @date 2022/7/6 13:29
 **/
public class HashAlgorithms {

    public static final HashAlgorithm MURMUR3_32 = openFht32(LongHashFunction::murmur_3, true);

    public static final HashAlgorithm MURMUR3_64 = openFht64(LongHashFunction::murmur_3, true);

    public static final HashAlgorithm XX_HASH_32 = apache32(XXHash32::new, true);

    public static final HashAlgorithm XX_HASH_64 = openFht64(LongHashFunction::xx, true);

    public static final HashAlgorithm XXH3_HASH_32 = openFht32(LongHashFunction::xx3, true);

    public static final HashAlgorithm XXH3_HASH_64 = openFht64(LongHashFunction::xx3, true);

    public static final HashAlgorithm CITY_HASH_32 = hash32(
            (value, seed) -> Integer.toUnsignedLong(HashUtil.cityHash32(value.getBytes(CHARSET))), false);

    public static final HashAlgorithm CITY_HASH_64 = hash64(
            (value, seed) -> HashUtil.cityHash64(value.getBytes(CHARSET), seed), true);

    public static final HashAlgorithm FARM_HASH_32 = openFht64(LongHashFunction::farmNa, true);

    public static final HashAlgorithm FARM_HASH_64 = openFht64(LongHashFunction::farmNa, true);

    public static final HashAlgorithm METRO_HASH_32 = openFht32(LongHashFunction::metro, true);

    public static final HashAlgorithm METRO_HASH_64 = openFht64(LongHashFunction::metro, true);

    private static final HashAlgorithm DEFAULT = XXH3_HASH_32;

    public static HashAlgorithm getDefault() {
        return DEFAULT;
    }

    private static HashAlgorithm openFht64(Function<Integer, LongHashFunction> hashBuilder, boolean enableSeed) {
        return new HashOpenHftAlgorithm(false, enableSeed, hashBuilder);
    }

    private static HashAlgorithm openFht32(Function<Integer, LongHashFunction> hashBuilder, boolean enableSeed) {
        return new HashOpenHftAlgorithm(true, enableSeed, hashBuilder);
    }

    private static HashAlgorithm apache32(Function<Integer, Checksum> hashFunction, boolean enableSeed) {
        return new HashApacheAlgorithm(true, enableSeed, hashFunction);
    }

    private static HashAlgorithm hash32(ToLongBiFunction<String, Integer> hashFunction, boolean enableSeed) {
        return new HashFunctionAlgorithm(true, enableSeed, hashFunction);
    }

    private static HashAlgorithm hash64(ToLongBiFunction<String, Integer> hashFunction, boolean enableSeed) {
        return new HashFunctionAlgorithm(false, enableSeed, hashFunction);
    }

    private static HashAlgorithm guava32(Function<Integer, HashFunction> hashFunction, boolean enableSeed) {
        return new HashGuavaAlgorithm(true, enableSeed, hashFunction);
    }

    private static HashAlgorithm guava64(Function<Integer, HashFunction> hashFunction, boolean enableSeed) {
        return new HashGuavaAlgorithm(false, enableSeed, hashFunction);
    }

    private static class HashOpenHftAlgorithm extends BaseHashAlgorithm {

        private final Function<Integer, LongHashFunction> hashBuilder;

        private HashOpenHftAlgorithm(boolean bit32, boolean enableSeed, Function<Integer, LongHashFunction> hashBuilder) {
            super(bit32, enableSeed);
            this.hashBuilder = hashBuilder;
        }

        @Override
        public long countHash(String key, int seed) {
            return hashBuilder.apply(seed).hashBytes(key.getBytes(CHARSET));
        }

    }

    private static class HashApacheAlgorithm extends BaseHashAlgorithm {

        private final Function<Integer, Checksum> hashFunction;

        private HashApacheAlgorithm(boolean bit32, boolean enableSeed, Function<Integer, Checksum> hashFunction) {
            super(bit32, enableSeed);
            this.hashFunction = hashFunction;
        }

        @Override
        public long countHash(String key, int seed) {
            var checksum = hashFunction.apply(seed);
            checksum.update(key.getBytes(CHARSET));
            return checksum.getValue();
        }

    }

    private static class HashGuavaAlgorithm extends BaseHashAlgorithm {

        private final Function<Integer, HashFunction> hashFunction;

        private HashGuavaAlgorithm(boolean bit32, boolean enableSeed, Function<Integer, HashFunction> hashFunction) {
            super(bit32, enableSeed);
            this.hashFunction = hashFunction;
        }

        @Override
        public long countHash(String key, int seed) {
            var function = hashFunction.apply(seed);
            return function.hashString(key, CHARSET).asLong();
        }

    }

    private static class HashFunctionAlgorithm extends BaseHashAlgorithm {

        private final ToLongBiFunction<String, Integer> hashFunction;

        private HashFunctionAlgorithm(boolean bit32, boolean enableSeed, ToLongBiFunction<String, Integer> hashFunction) {
            super(bit32, enableSeed);
            this.hashFunction = hashFunction;
        }

        @Override
        public long countHash(String key, int seed) {
            return hashFunction.applyAsLong(key, seed);
        }

    }

    private static abstract class BaseHashAlgorithm implements HashAlgorithm {

        public static final long MASH_32 = 0xFFFFFFFFL << 32;

        public static final long BIT_32_MAX = -1L >>> 32;

        private final boolean enableBit32;

        private final boolean enableSeed;

        protected BaseHashAlgorithm(boolean bit32, boolean enableSeed) {
            this.enableBit32 = bit32;
            this.enableSeed = enableSeed;
        }

        @Override
        public long hash(String key, int seed) {
            if (!enableSeed && seed != 0) {
                key += seed;
            }
            long code = countHash(key, seed);
            if (!enableBit32 || (code & MASH_32) == 0) {
                return code;
            } else {
                return code >>> 32;
            }
        }

        @Override
        public long getMax() {
            return enableBit32 ? BIT_32_MAX : Long.MAX_VALUE;
        }

        protected abstract long countHash(String key, int seed);

    }

    public static void main(String[] args) {

        //        System.out.println(XX_32_HASH.hash("ABC", 0));
        //        System.out.println(XX_64_HASH.hash("ABC", 0));
        //        System.out.println(XX3_32_HASH.hash("ABC", 0));
        //        System.out.println(XX3_64_HASH.hash("ABC", 0));

        for (int i = 0; i < 100; i++) {
            var data = new XXHash32();
            String value = "ABC___" + i;
            var code32 = MURMUR3_32.hash(value, 0);
            System.out.println("murmur_3_32 : " + value + " : " + code32);
        }

        for (int i = 0; i < 100; i++) {
            String value = "ABC___" + i;
            var code64 = MURMUR3_64.hash(value, 0);
            System.out.println("murmur_3_64 : " + value + " : " + code64);
        }

        for (int i = 0; i < 100; i++) {
            var data = new XXHash32();
            String value = "ABC___" + i;
            var code32 = XX_HASH_32.hash(value, 0);
            System.out.println("XX_32 : " + value + " : " + code32);
        }

        for (int i = 0; i < 100; i++) {
            String value = "ABC___" + i;
            var code64 = XX_HASH_64.hash(value, 0);
            System.out.println("XX_64 : " + value + " : " + code64);
        }

        for (int i = 0; i < 100; i++) {
            String value = "ABC___" + i;
            var code64 = XXH3_HASH_32.hash(value, 0);
            System.out.println("XX_3_32 : " + value + " : " + code64);
        }

        for (int i = 0; i < 100; i++) {
            String value = "ABC___" + i;
            var code64 = XXH3_HASH_64.hash(value, 0);
            System.out.println("XX_3_64 : " + value + " : " + code64);
        }

        for (int i = 0; i < 100; i++) {
            String value = "ABC___" + i;
            var code64 = CITY_HASH_32.hash(value, 0);
            System.out.println("CITY_32 : " + value + " : " + code64);
        }

        for (int i = 0; i < 100; i++) {
            String value = "ABC___" + i;
            var code64 = CITY_HASH_64.hash(value, 10);
            System.out.println("CITY_64 : " + value + " : " + code64);
        }

        for (int i = 0; i < 100; i++) {
            String value = "ABC___" + i;
            var code64 = METRO_HASH_32.hash(value, 0);
            System.out.println("METRO_32 : " + value + " : " + code64);
        }

        for (int i = 0; i < 100; i++) {
            String value = "ABC___" + i;
            var code64 = METRO_HASH_64.hash(value, 0);
            System.out.println("METRO_64 : " + value + " : " + code64);
        }

        for (int i = 0; i < 100; i++) {
            String value = "ABC___" + i;
            LongHashFunction.farmNa(0);
            var code64 = FARM_HASH_64.hash(value, 0);
            System.out.println("FARM_64 : " + " : " + code64);
        }
    }

}
