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
package com.tny.game.net.codec.verifier;

import com.tny.game.common.digest.binary.*;
import com.tny.game.net.codec.*;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * optimize-legacy-codec-paths 1.2：CRC64 去分配重构的数值金样锁。
 * 双保险：①测试内朴素 varargs 复刻 oracle（独立表生成与逐字节循环，不依赖被测类的链式入口）；
 * ②3 组固定输入的 8B 金样常量——重构后必须逐字节复现（设计决策 3：值等价的验收主体）。
 * 注：oracle 与生产共用同一算术右移表算法——金样锁定的正是"非标准但即契约"的当前值。
 */
class Crc64ValueGoldenTest {

    private static final long INITIAL = 0xFFFFFFFFFFFFFFFFL;
    private static final long POLY = 0x95AC9329AC4BC9B5L;
    private static final long[] TABLE = new long[256];

    static {
        for (int i = 0; i < 256; i++) {
            long part = i;
            for (int j = 0; j < 8; j++) {
                long x = ((int) part & 1) != 0 ? POLY : 0;
                part = (part >> 1) ^ x;
            }
            TABLE[i] = part;
        }
    }

    private static long update(long crc, byte[] data, int off, int len) {
        for (int i = off, end = off + len; i < end; i++) {
            crc = TABLE[(((int) crc) ^ data[i]) & 0xff] ^ (crc >> 8);
        }
        return crc;
    }

    private static byte[] oracleCrc64(DataPackageContext packager, byte[] body, int offset, int length) {
        byte[] numberBytes = BytesAide.int2Bytes(packager.getPacketNumber());
        byte[] codeBytes = BytesAide.int2Bytes(packager.getPacketCode());
        byte[] ak = packager.getAccessKeyBytes();
        long crc = update(INITIAL, numberBytes, 0, 4);
        crc = update(crc, body, offset, length);
        crc = update(crc, ak, 0, ak.length);
        crc = update(crc, codeBytes, 0, 4);
        return BytesAide.long2Bytes(crc);
    }

    private static byte[] bytes(int n) {
        byte[] b = new byte[n];
        for (int i = 0; i < n; i++) {
            b[i] = (byte) (i * 5 + 2);
        }
        return b;
    }

    private static DataPackageContext packager(long accessId, String key, int advance) {
        DataPackCodecOptions config = new DataPackCodecOptions();
        config.setSecurityKeys(new String[]{key});
        DataPackageContext packager = new DataPackageContext(accessId, config);
        for (int i = 0; i < advance; i++) {
            packager.nextNumber();
        }
        return packager;
    }

    /** 3 组固定输入（定 accessId/密钥/序号推进/载荷） */
    private static final String[] GOLDEN = {
        "1fc4cc899d2d4d78", "a11f4e8630a5510f", "947cf13d7140a6a5", "6551ed124bcf20a2",
    };

    @Test
    void productionEqualsNaiveOracleAndGolden() {
        CRC64CodecVerifier verifier = new CRC64CodecVerifier();
        byte[][] bodies = {
            "alpha-payload-0123456789".getBytes(StandardCharsets.UTF_8),
            new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16},
            "z".getBytes(StandardCharsets.UTF_8),
            bytes(333),
        };
        DataPackageContext[] packagers = {
            packager(111L, "bench-golden-key", 1),
            packager(987654321L, "another-key-x", 3),
            packager(42L, "k", 1),
            packager(24601L, "multi-wrap-key", 5),
        };
        for (int i = 0; i < bodies.length; i++) {
            byte[] produced = verifier.generate(packagers[i], bodies[i], 0, bodies[i].length);
            byte[] oracle = oracleCrc64(packagers[i], bodies[i], 0, bodies[i].length);
            assertArrayEquals(oracle, produced, "生产实现与朴素 oracle 漂移（v" + (i + 1) + "）");
            assertEquals(GOLDEN[i], BytesAide.toHexString(produced),
                    "金样漂移（v" + (i + 1) + "）——重构改变了 CRC64 输出值，立即回退");
        }
    }
}
