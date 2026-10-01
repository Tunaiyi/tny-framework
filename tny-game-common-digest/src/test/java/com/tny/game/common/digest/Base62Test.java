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
package com.tny.game.common.digest;

import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base62 编码/解码往返契约。
 * 编码字母表不含裸 '9'（'9' 是转义标志，仅以 "9A"/"9B"/"9C" 形式承载 61/62/63），
 * 修复点：输出缓冲由静态共享改为每次调用独立（原实现并发编码互相覆盖结果）。
 */
class Base62Test {

    /** 单字节全覆盖：0..255 每个字节独立 encode→decode 必须无损 */
    @Test
    void allSingleBytesRoundTrip() {
        for (int i = 0; i < 256; i++) {
            byte[] in = {(byte) i};
            String encoded = Base62.base62Encode(in);
            byte[] decoded = Base62.base62Decode(encoded.toCharArray());
            assertArrayEquals(in, decoded, "字节 " + i + " 经 '" + encoded + "' 往返丢失");
        }
    }

    /** 双字节尾组（覆盖 units 数=3 的尾部分支与跨标志组合） */
    @Test
    void twoByteTailRoundTrip() {
        byte[] in = {(byte) 0xFF, (byte) 0xFF}; // u1=63,u2=63,u3=60 → 含 "9C" 标志对
        byte[] decoded = Base62.base62Decode(Base62.base62Encode(in).toCharArray());
        assertArrayEquals(in, decoded);
    }

    /** 三字节整组（标准 4-unit 组）与含 0x3F/0xFC 边界的组合 */
    @Test
    void threeByteGroupRoundTrip() {
        byte[][] cases = {
                {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF},
                {0x00, 0x00, 0x00},
                {(byte) 0xFC, (byte) 0xC0, (byte) 0x03},
                {(byte) 0x9A, (byte) 0xBC, (byte) 0xDE},
        };
        for (byte[] in : cases) {
            assertArrayEquals(in, Base62.base62Decode(Base62.base62Encode(in).toCharArray()),
                    Arrays.toString(in) + " 往返丢失");
        }
    }

    /** 种子固定的随机多字节往返（1~30 长度混合，含 1/2 尾组） */
    @Test
    void randomizedRoundTrip() {
        Random random = new Random(620620620L);
        for (int len = 1; len <= 30; len++) {
            for (int trial = 0; trial < 50; trial++) {
                byte[] in = new byte[len];
                random.nextBytes(in);
                byte[] decoded = Base62.base62Decode(Base62.base62Encode(in).toCharArray());
                assertArrayEquals(in, decoded, "长度 " + len + " trial " + trial + " 往返丢失");
            }
        }
    }

    /** 不变式：编码输出中每个 '9' 必然后随 A/B/C（裸 '9' 不允许出现，否则解码侧标志语义被破坏） */
    @Test
    void nineIsAlwaysEscapePrefix() {
        Random random = new Random(1L);
        for (int i = 0; i < 2000; i++) {
            byte[] in = new byte[1 + random.nextInt(8)];
            random.nextBytes(in);
            String encoded = Base62.base62Encode(in);
            for (int p = encoded.indexOf('9'); p >= 0; p = encoded.indexOf('9', p + 1)) {
                assertTrue(p + 1 < encoded.length(), "尾部裸 '9' 转义不完整: " + encoded);
                char next = encoded.charAt(p + 1);
                assertTrue(next == 'A' || next == 'B' || next == 'C',
                        "'9' 后非 A/B/C，解码将 NPE: " + encoded);
            }
        }
    }

    /**
     * 并发契约（回归静态 StringBuilder 缺陷）：8 线程各编码各自的已知输入，
     * 断言每条输出与单线程结果逐字一致。
     */
    @Test
    void concurrentEncodeIsIsolated() throws Exception {
        int threads = 8;
        byte[][] inputs = new byte[threads][];
        String[] expected = new String[threads];
        for (int t = 0; t < threads; t++) {
            inputs[t] = ("payload-" + t + "-你好").getBytes(StandardCharsets.UTF_8);
            expected[t] = Base62.base62Encode(inputs[t]);
        }
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CyclicBarrier barrier = new CyclicBarrier(threads);
        AtomicInteger failures = new AtomicInteger();
        try {
            CountDownLatch done = new CountDownLatch(threads);
            for (int t = 0; t < threads; t++) {
                final int task = t;
                pool.execute(() -> {
                    try {
                        barrier.await(5, TimeUnit.SECONDS);
                        for (int round = 0; round < 200; round++) {
                            String out = Base62.base62Encode(inputs[task]);
                            if (!out.equals(expected[task])) {
                                failures.incrementAndGet();
                            }
                        }
                    } catch (Exception e) {
                        failures.incrementAndGet();
                    } finally {
                        done.countDown();
                    }
                });
            }
            assertTrue(done.await(30, TimeUnit.SECONDS), "并发编码任务超时");
        } finally {
            pool.shutdownNow();
        }
        assertEquals(0, failures.get(), "并发编码结果被互相覆盖");
    }

    /** 解码入口是 char[]：空输入得到空数组（不抛异常） */
    @Test
    void emptyInputRoundTrip() {
        assertEquals("", Base62.base62Encode(new byte[0]));
        assertArrayEquals(new byte[0], Base62.base62Decode(new char[0]));
    }

}
