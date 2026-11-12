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
 *
 * This file contains a port of the 32-bit CityHash function family from
 * Google's CityHash library (https://github.com/google/cityhash), which is
 * provided under the MIT license. The port preserves the reference
 * implementation's constants, length branches and arithmetic semantics.
 */
package com.tny.game.namespace.algorithm;

/**
 * 三十二位 CityHash 算法移植
 * <p>
 * 本类逐行移植 Google CityHash 参考实现（city.cpp）中的 {@code CityHash32} 函数族，
 * 输出与参考实现对同一字节序列的结果逐值相等。官方 32 位函数本身不接受种子参数，
 * 因此对种子的支持由 {@link HashAlgorithms} 的键拼接约定在算法层之外提供。
 * 三十二位无符号运算在 Java 中以 int 的回绕语义承载，对外出口一律折算为
 * 0 到 4294967295 区间的 long 返回值。
 * <p>
 * 类可见性收在本包内：对外契约面是 {@link HashAlgorithms#CITY_HASH_32} 常量，
 * 本类只是该常量的算法实现，不单独构成发布 API（原则卷 P10 三次法则：抽象要等重复出现）。
 *
 * @author kgtny
 * @date 2026/10/4 05:10
 **/
final class CityHash32 {

    /**
     * 三十二位哈希魔术常数，取自 Murmur3（参考实现 city.cpp 中的 c1 与 c2）。
     */
    private static final int C1 = 0xcc9e2d51;

    private static final int C2 = 0x1b873593;

    private static final int MUR_ADD = 0xe6546b64;

    private CityHash32() {
    }

    /**
     * 计算字节序列的三十二位 CityHash。
     *
     * @param data 待哈希的字节序列
     * @return 无符号三十二位哈希值，落在 0 到 4294967295 区间
     */
    static long hashUnsigned(byte[] data) {
        return Integer.toUnsignedLong(cityHash32(data, 0, data.length));
    }

    private static int cityHash32(byte[] s, int off, int len) {
        if (len <= 24) {
            if (len <= 12) {
                return len <= 4 ? hash32Len0to4(s, off, len) : hash32Len5to12(s, off, len);
            }
            return hash32Len13to24(s, off, len);
        }

        // len > 24：参考实现的主体循环形态，逐段读入并轮换 h、g、f 三个混入量。
        int h = len;
        int g = C1 * len;
        int f = g;
        int a0 = rotate32(fetch32(s, off + len - 4) * C1, 17) * C2;
        int a1 = rotate32(fetch32(s, off + len - 8) * C1, 17) * C2;
        int a2 = rotate32(fetch32(s, off + len - 16) * C1, 17) * C2;
        int a3 = rotate32(fetch32(s, off + len - 12) * C1, 17) * C2;
        int a4 = rotate32(fetch32(s, off + len - 20) * C1, 17) * C2;
        h ^= a0;
        h = rotate32(h, 19);
        h = h * 5 + MUR_ADD;
        h ^= a2;
        h = rotate32(h, 19);
        h = h * 5 + MUR_ADD;
        g ^= a1;
        g = rotate32(g, 19);
        g = g * 5 + MUR_ADD;
        g ^= a3;
        g = rotate32(g, 19);
        g = g * 5 + MUR_ADD;
        f += a4;
        f = rotate32(f, 19);
        f = f * 5 + MUR_ADD;
        int iters = (len - 1) / 20;
        int base = off;
        do {
            a0 = rotate32(fetch32(s, base) * C1, 17) * C2;
            a1 = fetch32(s, base + 4);
            a2 = rotate32(fetch32(s, base + 8) * C1, 17) * C2;
            a3 = rotate32(fetch32(s, base + 12) * C1, 17) * C2;
            a4 = fetch32(s, base + 16);
            h ^= a0;
            h = rotate32(h, 18);
            h = h * 5 + MUR_ADD;
            f += a1;
            f = rotate32(f, 19);
            f = f * C1;
            g += a2;
            g = rotate32(g, 18);
            g = g * 5 + MUR_ADD;
            h ^= a3 + a1;
            h = rotate32(h, 19);
            h = h * 5 + MUR_ADD;
            g ^= a4;
            g = Integer.reverseBytes(g) * 5;
            h += a4 * 5;
            h = Integer.reverseBytes(h);
            f += a0;
            // 参考实现的 PERMUTE3(f, h, g) 宏等价于 new f = 旧 g、new g = 旧 h、new h = 旧 f 的轮换。
            int tmp = f;
            f = g;
            g = h;
            h = tmp;
            base += 20;
        } while (--iters != 0);
        g = rotate32(g, 11) * C1;
        g = rotate32(g, 17) * C1;
        f = rotate32(f, 11) * C1;
        f = rotate32(f, 17) * C1;
        h = rotate32(h + g, 19);
        h = h * 5 + MUR_ADD;
        h = rotate32(h, 17) * C1;
        h = rotate32(h + f, 19);
        h = h * 5 + MUR_ADD;
        h = rotate32(h, 17) * C1;
        return h;
    }

    private static int hash32Len13to24(byte[] s, int off, int len) {
        int a = fetch32(s, off - 4 + (len >> 1));
        int b = fetch32(s, off + 4);
        int c = fetch32(s, off + len - 8);
        int d = fetch32(s, off + (len >> 1));
        int e = fetch32(s, off);
        int f = fetch32(s, off + len - 4);
        int h = len;
        return fmix(mur(f, mur(e, mur(d, mur(c, mur(b, mur(a, h)))))));
    }

    private static int hash32Len0to4(byte[] s, int off, int len) {
        int b = 0;
        int c = 9;
        for (int i = 0; i < len; i++) {
            // 参考实现按 signed char 取字节并做符号扩展，Java 的 byte 转 int 同语义。
            int v = s[off + i];
            b = b * C1 + v;
            c ^= b;
        }
        return fmix(mur(b, mur(len, c)));
    }

    private static int hash32Len5to12(byte[] s, int off, int len) {
        int a = len;
        int b = a * 5;
        int c = 9;
        int d = b;
        a += fetch32(s, off);
        b += fetch32(s, off + len - 4);
        c += fetch32(s, off + ((len >> 1) & 4));
        return fmix(mur(c, mur(b, mur(a, d))));
    }

    private static int mur(int a, int h) {
        a *= C1;
        a = rotate32(a, 17);
        a *= C2;
        h ^= a;
        h = rotate32(h, 19);
        return h * 5 + MUR_ADD;
    }

    private static int fmix(int h) {
        h ^= h >>> 16;
        h *= 0x85ebca6b;
        h ^= h >>> 13;
        h *= 0xc2b2ae35;
        h ^= h >>> 16;
        return h;
    }

    private static int rotate32(int val, int shift) {
        // 移位量为 0 时不执行 32 位移位（Java 与 C++ 同样把 32 位移位视为未定义/回绕，参考实现显式规避）。
        return shift == 0 ? val : ((val >>> shift) | (val << (32 - shift)));
    }

    /**
     * 按参考实现的小端约定读取四字节的无符号整数。
     */
    private static int fetch32(byte[] s, int i) {
        return (s[i] & 0xFF)
                | ((s[i + 1] & 0xFF) << 8)
                | ((s[i + 2] & 0xFF) << 16)
                | ((s[i + 3] & 0xFF) << 24);
    }

}
