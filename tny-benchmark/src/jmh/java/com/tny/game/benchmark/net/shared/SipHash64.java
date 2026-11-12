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
package com.tny.game.benchmark.net.shared;

/**
 * SipHash-2-4 实现（基准装配共用件）。
 * 自 VerifyAlgorithmsMicroBenchmark 摘出入 shared（split-bench-suites D6）：
 * 常规族矩阵 siphash 臂与开发测试族宿主共用，算法体零改动（le64 改为自带私有实现）。
 */
public final class SipHash64 {

    private SipHash64() {
    }

    public static long sipHash64(long k0, long k1, byte[] in, int off, int len) {
        long v0 = 0x736f6d6570736575L ^ k0;
        long v1 = 0x646f72616e646578L ^ k1;
        long v2 = 0x6c7967656e657261L ^ k0;
        long v3 = 0x7465646279746573L ^ k1;
        int left = len & ~7;
        int i = off;
        for (int n = 0; n < left; n += 8, i += 8) {
            long m = le64(in, i);
            v3 ^= m;
            v0 += v1; v1 = Long.rotateLeft(v1, 13); v1 ^= v0; v0 = Long.rotateLeft(v0, 41);
            v2 += v3; v3 = Long.rotateLeft(v3, 16); v3 ^= v2;
            v0 += v3; v3 = Long.rotateLeft(v3, 21); v3 ^= v0;
            v2 += v1; v1 = Long.rotateLeft(v1, 17); v1 ^= v2; v2 = Long.rotateLeft(v2, 32);
            v0 += v1; v1 = Long.rotateLeft(v1, 13); v1 ^= v0; v0 = Long.rotateLeft(v0, 41);
            v2 += v3; v3 = Long.rotateLeft(v3, 16); v3 ^= v2;
            v0 += v3; v3 = Long.rotateLeft(v3, 21); v3 ^= v0;
            v2 += v1; v1 = Long.rotateLeft(v1, 17); v1 ^= v2; v2 = Long.rotateLeft(v2, 32);
            v0 ^= m;
        }
        long m = ((long) len) << 56;
        for (int j = 0; j < (len & 7); j++) {
            m |= (in[off + left + j] & 0xFFL) << (8 * j);
        }
        v3 ^= m;
        v0 += v1; v1 = Long.rotateLeft(v1, 13); v1 ^= v0; v0 = Long.rotateLeft(v0, 41);
        v2 += v3; v3 = Long.rotateLeft(v3, 16); v3 ^= v2;
        v0 += v3; v3 = Long.rotateLeft(v3, 21); v3 ^= v0;
        v2 += v1; v1 = Long.rotateLeft(v1, 17); v1 ^= v2; v2 = Long.rotateLeft(v2, 32);
        v0 += v1; v1 = Long.rotateLeft(v1, 13); v1 ^= v0; v0 = Long.rotateLeft(v0, 41);
        v2 += v3; v3 = Long.rotateLeft(v3, 16); v3 ^= v2;
        v0 += v3; v3 = Long.rotateLeft(v3, 21); v3 ^= v0;
        v2 += v1; v1 = Long.rotateLeft(v1, 17); v1 ^= v2; v2 = Long.rotateLeft(v2, 32);
        v0 ^= m;
        v2 ^= 0xFF;
        for (int d = 0; d < 4; d++) {
            v0 += v1; v1 = Long.rotateLeft(v1, 13); v1 ^= v0; v0 = Long.rotateLeft(v0, 41);
            v2 += v3; v3 = Long.rotateLeft(v3, 16); v3 ^= v2;
            v0 += v3; v3 = Long.rotateLeft(v3, 21); v3 ^= v0;
            v2 += v1; v1 = Long.rotateLeft(v1, 17); v1 ^= v2; v2 = Long.rotateLeft(v2, 32);
        }
        return v0 ^ v1 ^ v2 ^ v3;
    }

    private static long le64(byte[] b, int i) {
        return (b[i] & 0xFFL) | ((b[i + 1] & 0xFFL) << 8) | ((b[i + 2] & 0xFFL) << 16) | ((b[i + 3] & 0xFFL) << 24)
                | ((b[i + 4] & 0xFFL) << 32) | ((b[i + 5] & 0xFFL) << 40) | ((b[i + 6] & 0xFFL) << 48) | ((b[i + 7] & 0xFFL) << 56);
    }
}
