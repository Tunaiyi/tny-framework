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

import com.tny.game.common.collection.empty.EmptyImmutableList;
import com.tny.game.common.collection.empty.EmptyImmutableMap;
import com.tny.game.common.collection.empty.EmptyImmutableSet;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EmptyImmutable 家族首写竞态契约（回归：List/Set 的 getWriter 无锁 check-then-act，
 * 并发首写各建各表互相覆盖丢数据；Map 已先行修复，三者行为必须一致）。
 * 使用并发安全容器作为 creator（生产形态：DefaultEventNotifier 即如此传入）。
 */
class EmptyImmutableConcurrencyTest {

    @Test
    void listConcurrentFirstAddKeepsAll() throws Exception {
        for (int round = 0; round < 50; round++) {
            EmptyImmutableList<Integer> list = new EmptyImmutableList<>(CopyOnWriteArrayList::new);
            int threads = 8;
            CountDownLatch start = new CountDownLatch(1);
            ExecutorService pool = Executors.newFixedThreadPool(threads);
            for (int t = 0; t < threads; t++) {
                final int value = t;
                pool.execute(() -> {
                    try {
                        start.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    list.add(value);
                });
            }
            start.countDown();
            pool.shutdown();
            assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
            assertEquals(threads, list.size(),
                    "并发首写各建各表互相覆盖（第 " + round + " 轮丢数据）");
        }
    }

    @Test
    void setConcurrentFirstAddKeepsAll() throws Exception {
        for (int round = 0; round < 50; round++) {
            EmptyImmutableSet<Integer> set = new EmptyImmutableSet<>(CopyOnWriteArraySet::new);
            int threads = 8;
            CountDownLatch start = new CountDownLatch(1);
            ExecutorService pool = Executors.newFixedThreadPool(threads);
            for (int t = 0; t < threads; t++) {
                final int value = t;
                pool.execute(() -> {
                    try {
                        start.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    set.add(value);
                });
            }
            start.countDown();
            pool.shutdown();
            assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
            assertEquals(threads, set.size(), "并发首写丢失元素（第 " + round + " 轮）");
        }
    }

    @Test
    void mapConcurrentFirstPutKeepsAll() throws Exception {
        EmptyImmutableMap<String, String> map = new EmptyImmutableMap<>(ConcurrentHashMap::new);
        int threads = 8;
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        for (int t = 0; t < threads; t++) {
            final int value = t;
            pool.execute(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                map.put("k" + value, "v" + value);
            });
        }
        start.countDown();
        pool.shutdown();
        assertTrue(pool.awaitTermination(10, TimeUnit.SECONDS));
        assertEquals(threads, map.size(), "Map 必须与 List/Set 行为一致");
    }

    /** 只读视图：空态不物化（size/contains 不触发换表） */
    @Test
    void readOpsDoNotMaterialize() {
        EmptyImmutableList<String> list = new EmptyImmutableList<>();
        assertTrue(list.isEmpty());
        assertFalse(list.contains("x"));
        assertEquals(0, list.size());
        // 读后写仍正确
        assertTrue(list.add("x"));
        assertEquals(1, list.size());
        assertEquals(List.of("x"), new ArrayList<>(list));
    }

}
