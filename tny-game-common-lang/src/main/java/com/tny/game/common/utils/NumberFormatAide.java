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
        for (int i = 1; i <= LONG_MAX_DIGITS; i++) {
            step *= 10;
            digitsMap.put(step, i);
        }
        digitsMap.put(Long.MAX_VALUE, LONG_MAX_DIGITS);

        ZERO_FILL = ImmutableList.copyOf(zeroFill);
        DIGITS_MAP = ImmutableSortedMap.copyOf(digitsMap);
    }

    public static String alignDigits(long hashCode, long maxCode) {
        int digits = DIGITS_MAP.higherEntry(hashCode).getValue();
        int maxDigits = DIGITS_MAP.higherEntry(maxCode).getValue();
        int lack = maxDigits - digits;
        if (lack == 0) {
            return String.valueOf(hashCode);
        }
        return ZERO_FILL.get(lack) + hashCode;
    }

}
