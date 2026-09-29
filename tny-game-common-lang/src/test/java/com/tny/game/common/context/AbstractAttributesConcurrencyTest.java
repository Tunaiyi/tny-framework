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
package com.tny.game.common.context;

import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 组 15（Wave-C）红灯基线：属性容器并发读写安全（net-session"会话属性容器并发安全"契约）。
 * 修复前：computeIfAbsent/setIfAbsent 对裸 HashMap 无锁操作，且 getMap 懒创建本身无同步——
 * 并发首写各自建表互相覆盖（整表丢失）、与加锁的 setAttribute 混用可损坏桶结构。
 */
class AbstractAttributesConcurrencyTest {

    private static final class TestAttributes extends AbstractAttributes {
    }

    private static AttrKey<String> key(int i) {
        return AttrKeys.key("attr-" + i);
    }

    @Test
    @DisplayName("并发首写（含 computeIfAbsent 与 setAttribute 混用）：全部写入可回读")
    void concurrentFirstWritesAreAllVisible() throws Exception {
        TestAttributes attributes = new TestAttributes();
        int threads = 16;
        int perThread = 64;
        CyclicBarrier barrier = new CyclicBarrier(threads);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            int tid = t;
            futures.add(pool.submit(() -> {
                try {
                    barrier.await(5, TimeUnit.SECONDS);
                } catch (Exception ignored) {
                }
                for (int i = 0; i < perThread; i++) {
                    int index = tid * perThread + i;
                    if (index % 3 == 0) {
                        attributes.computeIfAbsent(key(index), "v" + index);
                    } else if (index % 3 == 1) {
                        attributes.setIfAbsent(key(index), "v" + index);
                    } else {
                        attributes.setAttribute(key(index), "v" + index);
                    }
                }
                return null;
            }));
        }
        pool.shutdown();
        assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS));
        for (Future<?> f : futures) {
            f.get(5, TimeUnit.SECONDS);
        }
        for (int index = 0; index < threads * perThread; index++) {
            assertEquals("v" + index, attributes.getAttribute(key(index)), "第 " + index + " 号写入丢失（并发首写覆盖）");
        }
    }

    @Test
    @DisplayName("computeIfAbsent 原子性：同 key 并发只产生一个值")
    void computeIfAbsentIsAtomicPerKey() throws Exception {
        TestAttributes attributes = new TestAttributes();
        int threads = 8;
        CyclicBarrier barrier = new CyclicBarrier(threads);
        CountDownLatch done = new CountDownLatch(threads);
        Set<String> winners = ConcurrentHashMap.newKeySet();
        for (int t = 0; t < threads; t++) {
            new Thread(() -> {
                try {
                    barrier.await(5, TimeUnit.SECONDS);
                    winners.add(attributes.computeIfAbsent(key(999), () -> "winner"));
                } catch (Exception ignored) {
                } finally {
                    done.countDown();
                }
            }).start();
        }
        assertTrue(done.await(10, TimeUnit.SECONDS));
        assertEquals(1, winners.size(), "同 key 并发 compute 必须唯一胜者，实际 " + winners);
    }

    @Test
    @DisplayName("兼容锚：null 值 setAttribute 语义保持（get 返回 null、remove 可清）")
    void nullValueSemanticsPreserved() {
        TestAttributes attributes = new TestAttributes();
        attributes.setAttribute(key(1), "x");
        attributes.setAttribute(key(1), null);
        assertNull(attributes.getAttribute(key(1)));
        attributes.setAttribute(key(2), "x");
        assertEquals("x", attributes.getAttribute(key(2)));
    }

}
