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
