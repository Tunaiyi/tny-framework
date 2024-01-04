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

package com.tny.game.common.collection;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2020-03-25 02:23
 */
public class ArrayAide {

    public static int[] intArray(int... value) {
        return value;
    }

    public static long[] longArray(long... value) {
        return value;
    }

    public static double[] doubleArray(double... value) {
        return value;
    }

    public static float[] floatArray(float... value) {
        return value;
    }

    public static short[] shortArray(short... value) {
        return value;
    }

    public static String[] stringArray(String... value) {
        return value;
    }

    @SafeVarargs
    public static <T> T[] objectArray(T... value) {
        return value;
    }

}
