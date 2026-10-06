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
package com.tny.game.common.concurrent.utils;

import org.junit.jupiter.api.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.StampedLock;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 乐观读工具类契约测试（design D2）：业务执行发生在读校验之前，
 * 校验失败必须降级为悲观读锁并重跑业务；无并发写时业务恰好执行一次。
 * <p>
 * 契约前提：供应商幂等/无副作用（javadoc 契约）。
 */
class StampedLockAideTest {

    /**
     * 写锁方法必须真互斥（修复记录补充：原实现误用乐观读/悲观读，临界区可交错；
     * 见 red-baseline.md 追加节）。业务可含副作用与复合读写。
     */
    @Test
    void writeLockMethodsProvideMutualExclusion() throws Exception {
        StampedLock lock = new StampedLock();
        AtomicInteger inside = new AtomicInteger();
        AtomicInteger maxConcurrent = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(2);
        Runnable critical = () -> StampedLockAide.supplyInWriteLock(lock, () -> {
            int now = inside.incrementAndGet();
            maxConcurrent.accumulateAndGet(now, Math::max);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            inside.decrementAndGet();
            return null;
        });
        try {
            pool.submit(critical);
            pool.submit(critical);
            pool.shutdown();
            assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));
        } finally {
            pool.shutdownNow();
        }
        assertEquals(1, maxConcurrent.get(), "写锁临界区任意时刻至多一个线程");
    }

    @Test
    void supplyRunsExactlyOnceWithoutConcurrentWrite() {
        StampedLock lock = new StampedLock();
        AtomicInteger runs = new AtomicInteger();
        int result = StampedLockAide.supplyInOptimisticReadLock(lock, () -> runs.incrementAndGet() * 7);
        assertEquals(1, runs.get(), "无并发写时业务应恰好执行一次");
        assertEquals(7, result);
    }

    @Test
    void supplyFallsBackAndRerunsUnderWriteRace() throws Exception {
        StampedLock lock = new StampedLock();
        AtomicInteger runs = new AtomicInteger();
        // 写线程先持写锁 200ms：乐观读路径必然执行后校验失败，降级为悲观读锁重跑
        runWithHeldWriteLock(lock, () -> {
            int result = StampedLockAide.supplyInOptimisticReadLock(lock, () -> runs.incrementAndGet());
            assertEquals(runs.get(), result, "返回值必须来自最后一次实际执行");
        });
        assertEquals(2, runs.get(),
                "业务先无锁执行一次，validate 失败后在悲观读锁下重跑一次，共两次");
    }

    @Test
    void runFallsBackAndRerunsUnderWriteRace() throws Exception {
        StampedLock lock = new StampedLock();
        AtomicInteger runs = new AtomicInteger();
        runWithHeldWriteLock(lock, () ->
                StampedLockAide.runInOptimisticReadLock(lock, runs::incrementAndGet));
        assertEquals(2, runs.get(), "runInOptimisticReadLock 同契约：竞态下业务执行两次");
    }

    @Test
    void callFallsBackAndRerunsUnderWriteRace() throws Exception {
        StampedLock lock = new StampedLock();
        AtomicInteger runs = new AtomicInteger();
        runWithHeldWriteLock(lock, () -> {
            try {
                int result = StampedLockAide.callInOptimisticReadLock(lock, runs::incrementAndGet);
                assertEquals(runs.get(), result);
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        });
        assertEquals(2, runs.get(), "callInOptimisticReadLock 同契约：竞态下业务执行两次");
    }

    /**
     * 启动线程持有写锁约 200ms，期间在主线程执行 action（其内部乐观读必然遭遇
     * "执行后校验失败"），随后写锁释放使悲观读锁得以继续。
     */
    private static void runWithHeldWriteLock(StampedLock lock, Runnable action) throws Exception {
        CountDownLatch holding = new CountDownLatch(1);
        Thread writer = new Thread(() -> {
            long stamp = lock.writeLock();
            try {
                holding.countDown();
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlockWrite(stamp);
            }
        }, "stampedlock-write-holder");
        writer.start();
        assertTrue(holding.await(5, TimeUnit.SECONDS));
        action.run();
        writer.join(5_000);
        assertFalse(writer.isAlive());
    }

}
