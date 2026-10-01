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

import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 雪花回拨分支票型契约（identifier-generation 二轮 REAL_GAP 2）：
 * 回拨等待解锁路径此前按票种解锁（unlockRead），若此刻持有的是写锁票（降读→排队取写成功后
 * 再撞回拨）必抛 IllegalMonitorStateException，取号线程被连坐切断。
 * 修复方向：continue 前按当前持有票种通用解锁 unlock(lockStamp)。
 */
class SnowflakeRollbackTicketTest {

    /** 高位段 workerID，避开其他测试用例占用区间（RESERVED 全局登记） */
    private static final AtomicLong FREE_WORKER_ID = new AtomicLong(3500);

    private static final long T0 = 1_800_000_000_000L;

    /**
     * 确定性编排：seed 线程先把 lastTimestamp 钉到 T0；holder 线程在读锁持有期阻塞于时钟源，
     * 迫使 main 线程走"tryConvert 失败→释放读票→取写票"分支；main 在写票持有的下一轮迭代
     * 读到脚本回拨值（T0-80，阈值内）→回拨分支必须以当前票种解锁。
     * 修复前：main 以 unlockRead(写票) 解锁 → IllegalMonitorStateException。
     * 修复后：main 无异常完成恢复产号，号可解析且不重复。
     */
    @Test
    void rollbackUnderWriteStampRecoversWithoutTicketMismatch() throws Exception {
        long workerId = FREE_WORKER_ID.getAndIncrement();
        CountDownLatch holderHolding = new CountDownLatch(1);
        CountDownLatch holderRelease = new CountDownLatch(1);
        AtomicInteger mainCalls = new AtomicInteger();
        AtomicInteger holderCalls = new AtomicInteger();
        Supplier<Long> clock = () -> {
            String name = Thread.currentThread().getName();
            if (name.startsWith("sf-main")) {
                int call = mainCalls.incrementAndGet();
                if (call == 1) {
                    return T0;            // 首轮：与 lastTimestamp 同毫秒 → 走转换路径
                }
                if (call == 2) {
                    return T0 - 80;       // 次轮（写票持有中）：注入阈值内回拨 → 旧代码在此票型连坐
                }
                return T0 + 80 + call;    // 追平并继续前进（等待自旋有界）
            }
            if (name.startsWith("sf-holder")) {
                holderHolding.countDown();
                try {
                    assertTrue(holderRelease.await(5, TimeUnit.SECONDS), "holder 释放超界");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return T0 + holderCalls.incrementAndGet();
            }
            return T0; // seed
        };
        SnowflakeIdCreator creator = new SnowflakeIdCreator(workerId, 12L, 10L, clock);

        // seed：把 lastTimestamp 钉到 T0（真实线程走一遍完整产号）
        long seedId = creator.createId();
        assertTrue(SnowflakeIdCreator.parseTime(seedId) >= T0, "seed 前提失败");

        Thread holder = new Thread(() -> creator.createId(), "sf-holder-1");
        holder.setUncaughtExceptionHandler((t, e) -> {
        });
        holder.start();
        assertTrue(holderHolding.await(5, TimeUnit.SECONDS), "holder 未进入读锁持有");

        AtomicReference<Throwable> mainFailure = new AtomicReference<>();
        AtomicLong mainId = new AtomicLong(-1L);
        Thread main = new Thread(() -> {
            try {
                mainId.set(creator.createId());
            } catch (Throwable e) {
                mainFailure.set(e);
            }
        }, "sf-main-1");
        main.start();
        Thread.sleep(120); // main 应已停在 holder 的读锁之后（取写票排队）
        holderRelease.countDown();

        main.join(10_000);
        holder.join(10_000);
        assertFalse(main.isAlive(), "main 未在界内结束（回拨等待或锁票互等失控）");
        assertFalse(holder.isAlive(), "holder 未在界内结束");
        assertNull(mainFailure.get(), "写票持有时撞回拨：解锁票型与持有票型不符，取号线程被连坐");
        assertTrue(mainId.get() >= 0L);
        assertTrue(SnowflakeIdCreator.parseTime(mainId.get()) >= T0,
                "回拨恢复后不得用回拨时刻产号（时间位必须已追平）");
        assertNotEquals(seedId, mainId.get(), "恢复产号与历史号重复");
    }

    /**
     * 并发不被连坐（有界）：一个线程在产号中途注入阈值内回拨恢复，其余线程同期正常取号——
     * 全部线程必须无异常完成且零重号。
     */
    @Test
    void concurrentGeneratorsNotCollaterallyDamagedByRollback() throws Exception {
        long workerId = FREE_WORKER_ID.getAndIncrement();
        ThreadLocal<Integer> rollerCalls = ThreadLocal.withInitial(() -> 0);
        Supplier<Long> clock = () -> {
            long now = System.currentTimeMillis();
            if (Thread.currentThread().getName().startsWith("sf-roller")) {
                int c = rollerCalls.get() + 1;
                rollerCalls.set(c);
                // 周期性注入小幅回拨（阈值内），反复踩回拨分支
                if (c % 7 == 3) {
                    return now - 100;
                }
            }
            return now;
        };
        SnowflakeIdCreator creator = new SnowflakeIdCreator(workerId, 12L, 10L, clock);

        int threads = 4;
        int per = 400;
        CountDownLatch start = new CountDownLatch(1);
        AtomicReference<Throwable> failure = new AtomicReference<>();
        List<Set<Long>> results = new ArrayList<>();
        List<Thread> workers = new ArrayList<>();
        for (int t = 0; t < threads; t++) {
            Thread thread = new Thread(() -> {
                Set<Long> own = ConcurrentHashMap.newKeySet();
                try {
                    start.await();
                    for (int i = 0; i < per; i++) {
                        own.add(creator.createId());
                    }
                } catch (Throwable e) {
                    failure.compareAndSet(null, e);
                } finally {
                    synchronized (results) {
                        results.add(own);
                    }
                }
            }, t == 0 ? "sf-roller-0" : "sf-normal-" + t);
            workers.add(thread);
            thread.start();
        }
        start.countDown();
        for (Thread thread : workers) {
            thread.join(30_000);
            assertFalse(thread.isAlive(), "取号线程未在界内结束（回拨恢复失控或锁票互等）");
        }
        assertNull(failure.get(), "并发取号被回拨恢复线程连坐（票型解锁异常）");
        Set<Long> all = new HashSet<>();
        for (Set<Long> own : results) {
            all.addAll(own);
        }
        assertEquals(threads * per, all.size(), "并发产号出现重号");
    }

}
