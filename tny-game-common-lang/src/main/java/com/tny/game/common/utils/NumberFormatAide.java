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

package com.tny.game.common.utils;

import com.google.common.collect.*;

import java.util.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2022/7/20 17:05
 **/
public final class NumberFormatAide {

    private static final int LONG_MAX_DIGITS = String.valueOf(Long.MAX_VALUE).length();

    private static final NavigableMap<Long, Integer> DIGITS_MAP;

    private static final List<String> ZERO_FILL;

    public NumberFormatAide() {
    }

    static {
        StringBuilder builder = new StringBuilder();
        var zeroFill = new ArrayList<String>();
        for (int index = 0; index < LONG_MAX_DIGITS; index++) {
            if (index > 0) {
                builder.append(0);
            }
            zeroFill.add(builder.toString());
        }
        long step = 1L;
        NavigableMap<Long, Integer> digitsMap = new TreeMap<>();
        // 原循环到 i==19 时 step*=10 溢出为负数入表（10^19 > Long.MAX_VALUE）
        for (int i = 1; i < LONG_MAX_DIGITS; i++) {
            step *= 10;
            digitsMap.put(step, i);
        }
        digitsMap.put(Long.MAX_VALUE, LONG_MAX_DIGITS);

        ZERO_FILL = ImmutableList.copyOf(zeroFill);
        DIGITS_MAP = ImmutableSortedMap.copyOf(digitsMap);
    }

    public static String alignDigits(long hashCode, long maxCode) {
        // Long.MAX_VALUE 的 higherEntry 为 null（原实现直接 NPE）
        Map.Entry<Long, Integer> digitsEntry = DIGITS_MAP.higherEntry(hashCode);
        int digits = digitsEntry != null ? digitsEntry.getValue() : LONG_MAX_DIGITS;
        Map.Entry<Long, Integer> maxEntry = DIGITS_MAP.higherEntry(maxCode);
        int maxDigits = maxEntry != null ? maxEntry.getValue() : LONG_MAX_DIGITS;
        int lack = maxDigits - digits;
        if (lack <= 0) {
            // 位数已达标（含 hashCode 位数超出 maxCode 位数的降级场景，原实现负索引越界）
            return String.valueOf(hashCode);
        }
        return ZERO_FILL.get(lack) + hashCode;
    }

}
