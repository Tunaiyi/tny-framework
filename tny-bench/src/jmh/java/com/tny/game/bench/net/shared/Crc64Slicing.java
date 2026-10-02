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
package com.tny.game.bench.net.shared;

/**
 * 与生产 CRC64 同多项式（ECMA-182 反射）的 slicing-by-8 原型。
 * 8 张表：t[0] 即逐字节表，t[k][i] = (t[k-1][i] >>> 8) ^ t[0][t[k-1][i] & 0xFF]。
 * 自 CryptoAlgorithmMicroBenchmark 摘出入 shared（split-bench-suites D6）：
 * 常规族矩阵臂与开发测试族宿主共用，算法体零改动（le64 改为自带私有实现）。
 */
public final class Crc64Slicing {

    /** 标准 CRC64 初值（自宿主类随摘出迁入）。 */
    public static final long CRC64_INITIAL = 0xFFFFFFFFFFFFFFFFL;

    private final long[][] tables = new long[8][256];

    public Crc64Slicing() {
        long poly = 0x95AC9329AC4BC9B5L;
        for (int i = 0; i < 256; i++) {
            long part = i;
            for (int j = 0; j < 8; j++) {
                part = ((part & 1) != 0) ? (part >>> 1) ^ poly : part >>> 1;
            }
            tables[0][i] = part;
        }
        for (int i = 0; i < 256; i++) {
            long c = tables[0][i];
            for (int k = 1; k < 8; k++) {
                c = (c >>> 8) ^ tables[0][(int) c & 0xFF];
                tables[k][i] = c;
            }
        }
    }

    /** 本表语义（标准 CRC64，逻辑右移）下的逐字节参照实现 */
    public long updateSlow(long crc, byte[] data, int off, int len) {
        for (int i = off, end = off + len; i < end; i++) {
            crc = tables[0][((int) crc ^ data[i]) & 0xFF] ^ (crc >>> 8);
        }
        return crc;
    }

    public long update(long crc, byte[] data, int off, int len) {
        int i = off;
        int end = off + len;
        for (; i + 8 <= end; i += 8) {
            crc ^= le64(data, i);
            crc = tables[7][(int) crc & 0xFF] ^ tables[6][(int) (crc >>> 8) & 0xFF]
                    ^ tables[5][(int) (crc >>> 16) & 0xFF] ^ tables[4][(int) (crc >>> 24) & 0xFF]
                    ^ tables[3][(int) (crc >>> 32) & 0xFF] ^ tables[2][(int) (crc >>> 40) & 0xFF]
                    ^ tables[1][(int) (crc >>> 48) & 0xFF] ^ tables[0][(int) (crc >>> 56) & 0xFF];
        }
        for (; i < end; i++) {
            crc = tables[0][((int) crc ^ data[i]) & 0xFF] ^ (crc >>> 8);
        }
        return crc;
    }

    private static long le64(byte[] b, int i) {
        return (b[i] & 0xFFL) | ((b[i + 1] & 0xFFL) << 8) | ((b[i + 2] & 0xFFL) << 16) | ((b[i + 3] & 0xFFL) << 24)
                | ((b[i + 4] & 0xFFL) << 32) | ((b[i + 5] & 0xFFL) << 40) | ((b[i + 6] & 0xFFL) << 48) | ((b[i + 7] & 0xFFL) << 56);
    }
}
