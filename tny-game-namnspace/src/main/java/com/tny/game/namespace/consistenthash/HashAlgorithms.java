package com.tny.game.namespace.consistenthash;

import net.openhft.hashing.LongHashFunction;

import java.nio.charset.*;
import java.util.*;
import java.util.function.Function;

/**
 * 哈希算法
 * <p>
 *
 * @author kgtny
 * @date 2022/7/6 13:29
 **/
public class HashAlgorithms {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

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

    private static final int LONG_MAX_DIGITS = String.valueOf(Long.MAX_VALUE).length();

    private static final String[] ZERO_FILL = new String[LONG_MAX_DIGITS];

    public static String alignDigits(long hashCode, long maxCode) {
        int maxDigits = digitsMap.higherEntry(maxCode).getValue();
        int digits = digitsMap.higherEntry(hashCode).getValue();
        int lack = maxDigits - digits;
        if (lack == 0) {
            return String.valueOf(hashCode);
        }
        return ZERO_FILL[lack] + hashCode;
    }

    private static final NavigableMap<Long, Integer> digitsMap = new TreeMap<>();
    static {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < LONG_MAX_DIGITS; index++) {
            if (index > 0) {
                builder.append(0);
            }
            ZERO_FILL[index] = builder.toString();
        }

        long step = 1L;
        for (int i = 1; i <= LONG_MAX_DIGITS; i++) {
            step *= 10;
            digitsMap.put(step, i);
        }
        digitsMap.put(Long.MAX_VALUE, LONG_MAX_DIGITS);
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

        @Override
        public String alignDigits(long hashCode) {
            int digits = digitsMap.higherEntry(hashCode).getValue();
            int lack = LONG_MAX_DIGITS - digits;
            if (lack == 0) {
                return String.valueOf(hashCode);
            }
            return ZERO_FILL[lack] + hashCode;
        }

    }

    private static class Hash32FunctionHashAlgorithm implements HashAlgorithm {

        public static final long MAX = -1L >>> 32;

        private static final int MAX_DIGITS = String.valueOf(MAX).length();

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

        @Override
        public String alignDigits(long hashCode) {
            int digits = digitsMap.higherEntry(hashCode).getValue();
            int lack = MAX_DIGITS - digits;
            if (lack == 0) {
                return String.valueOf(hashCode);
            }
            return ZERO_FILL[lack] + hashCode;
        }

    }

}
