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
package com.tny.game.common.lock;

import com.tny.game.common.concurrent.lock.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 批量锁并发编排契约测试（复活自整文件注释的旧版 CollectionLockTest，
 * fix-common-dormant-defects · lock-facilities"测试复活"Requirement）。
 * 手法约束：栅栏 + 有界等待 + 原子计数，不以睡眠猜测判定。
 */
class CollectionLockTest {

    static class Entity implements LockEntity<Integer> {
        private final int id;

        Entity(int id) {
            this.id = id;
        }

        @Override
        public Integer getIdentity() {
            return id;
        }
    }

    private static List<LockEntity<?>> batch(int... ids) {
        List<LockEntity<?>> list = new ArrayList<>();
        for (int id : ids) {
            list.add(new Entity(id));
        }
        return list;
    }

    /** 临界区进入编排器：记录并发驻留峰值与完成数 */
    private static final class Residency {

        final AtomicInteger current = new AtomicInteger();

        final AtomicInteger max = new AtomicInteger();

        final AtomicInteger writeResident = new AtomicInteger();

        final AtomicInteger maxWithWrite = new AtomicInteger();

        final AtomicInteger completed = new AtomicInteger();

        /** 违规快照：写持有期间总驻留>1，或写并发>1 */
        final AtomicBoolean writeViolation = new AtomicBoolean();

        void enter(boolean isWrite) {
            int now = current.incrementAndGet();
            max.accumulateAndGet(now, Math::max);
            if (isWrite) {
                int w = writeResident.incrementAndGet();
                maxWithWrite.accumulateAndGet(Math.max(w, now), Math::max);
                if (w > 1 || now > 1) {
                    writeViolation.set(true);
                }
            } else if (writeResident.get() > 0) {
                writeViolation.set(true);
            }
        }

        void exit(boolean isWrite) {
            current.decrementAndGet();
            if (isWrite) {
                writeResident.decrementAndGet();
            }
            completed.incrementAndGet();
        }

        void enter() {
            enter(false);
        }

        void exit() {
            exit(false);
        }
    }

    /** 读-读交错：并行驻留 2 */
    @Test
    void readReadRunsInParallel() throws Exception {
        Residency residency = new Residency();
        CyclicBarrier bothWantIn = new CyclicBarrier(2);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int t = 0; t < 2; t++) {
                futures.add(pool.submit(() -> {
                    Lock lock = LockAide.getReadLock(batch(1, 2));
                    lock.lock();
                    try {
                        // 双方都在读区等待会合：读读并行则栅栏达成；被互斥则超时
                        residency.enter();
                        try {
                            // 栅栏会合成功=两读者同时驻留；被互斥则对方进不来→超时
                            bothWantIn.await(3, TimeUnit.SECONDS);
                        } catch (TimeoutException e) {
                            // 互斥证据：另一读者没能与我并行
                        } catch (BrokenBarrierException e) {
                            throw new AssertionError(e);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        residency.exit();
                        lock.unlock();
                    }
                }));
            }
            for (Future<?> f : futures) {
                f.get(10, TimeUnit.SECONDS);
            }
        } finally {
            pool.shutdownNow();
        }
        assertEquals(2, residency.max.get(), "读读必须并行驻留");
    }

    /** 写-写、写-读、读-写交错：任意时刻驻留 1，完成数精确 */
    @Test
    void exclusiveMixturesNeverOverlap() throws Exception {
        List<LockEntity[]> plan = List.of(
                batch(10).toArray(new LockEntity<?>[0]),
                batch(10).toArray(new LockEntity<?>[0]));
        Residency residency = new Residency();
        int writers = 2;
        int readers = 2;
        int perThread = 20;
        CountDownLatch done = new CountDownLatch(writers + readers);
        ExecutorService pool = Executors.newFixedThreadPool(writers + readers);
        try {
            for (int w = 0; w < writers; w++) {
                pool.execute(() -> {
                    for (int i = 0; i < perThread; i++) {
                        Lock lock = LockAide.getWriteLock(plan.get(0));
                        lock.lock();
                        try {
                            residency.enter(true);
                            Thread.yield();
                        } finally {
                            residency.exit(true);
                            lock.unlock();
                        }
                    }
                    done.countDown();
                });
            }
            for (int r = 0; r < readers; r++) {
                pool.execute(() -> {
                    for (int i = 0; i < perThread; i++) {
                        Lock lock = LockAide.getReadLock(plan.get(1));
                        lock.lock();
                        try {
                            residency.enter();
                            Thread.yield();
                        } finally {
                            residency.exit();
                            lock.unlock();
                        }
                    }
                    done.countDown();
                });
            }
            assertTrue(done.await(20, TimeUnit.SECONDS), "读写交错编排未在时限内完成");
        } finally {
            pool.shutdownNow();
        }
        assertFalse(residency.writeViolation.get(), "写持有期间不得有任何并发驻留（含读者）");
        assertTrue(residency.max.get() >= 2, "读读应可并行驻留（观测不到任何并行说明互斥过紧）");
        assertEquals((writers + readers) * perThread, residency.completed.get());
    }

    /** 可中断获取：被中断者抛中断异常、恢复中断标志、锁完整归还 */
    @Test
    void interruptibleAcquisitionRollsBackOnInterrupt() throws Exception {
        Lock held = LockAide.getWriteLock(batch(77));
        held.lock();
        AtomicReference<Throwable> thrown = new AtomicReference<>();
        AtomicBoolean flagKept = new AtomicBoolean();
        Thread victim = new Thread(() -> {
            Lock lock = LockAide.getWriteLock(batch(77));
            try {
                lock.lockInterruptibly();
            } catch (Throwable e) {
                thrown.set(e);
                flagKept.set(Thread.currentThread().isInterrupted());
            }
        });
        victim.start();
        Thread.sleep(150);
        victim.interrupt();
        victim.join(5000);
        held.unlock();
        assertInstanceOf(InterruptedException.class, thrown.get());
        assertTrue(flagKept.get(), "中断标志必须恢复");
        // 锁状态完整归还：后续获取立即成功
        Lock followUp = LockAide.getWriteLock(batch(77));
        assertTrue(followUp.tryLock(2, TimeUnit.SECONDS));
        followUp.unlock();
    }

    /** 即时尝试失败与限时超时：他线程尝试返回 false、锁未被占用（同线程写锁可重入不构成探测） */
    @Test
    void tryLockFailuresAreClean() throws Exception {
        Lock holder = LockAide.getWriteLock(batch(88));
        holder.lock();
        ExecutorService probe = Executors.newSingleThreadExecutor();
        try {
            try {
                assertFalse(probe.submit(() -> {
                    Lock immediate = LockAide.getWriteLock(batch(88));
                    boolean got = immediate.tryLock();
                    if (got) {
                        immediate.unlock();
                    }
                    return got;
                }).get(10, TimeUnit.SECONDS), "被占用时即时尝试必须失败");
                assertFalse(probe.submit(() -> {
                    Lock bounded = LockAide.getWriteLock(batch(88));
                    boolean got = bounded.tryLock(80, TimeUnit.MILLISECONDS);
                    if (got) {
                        bounded.unlock();
                    }
                    return got;
                }).get(10, TimeUnit.SECONDS), "超时尝试必须失败");
            } finally {
                holder.unlock();
            }
            assertTrue(probe.submit(() -> {
                Lock afterRelease = LockAide.getWriteLock(batch(88));
                boolean got = afterRelease.tryLock(2, TimeUnit.SECONDS);
                if (got) {
                    afterRelease.unlock();
                }
                return got;
            }).get(10, TimeUnit.SECONDS), "释放后立即窗口内可获取");
        } finally {
            probe.shutdownNow();
        }
    }

    /** 混合读写批整体获取：只升写，释放干净，无非法监视器状态 */
    @Test
    void mixedReadWriteBatchPairsCleanly() {
        Lock mixed = LockAide.getLock(batch(99), batch(99, 100));
        assertDoesNotThrow(() -> {
            mixed.lock();
            mixed.unlock();
        });
        // 再走一轮读批确认释放无残留
        Lock readAgain = LockAide.getReadLock(batch(99));
        assertDoesNotThrow(() -> {
            readAgain.lock();
            readAgain.unlock();
        });
    }

    /** 零目标建链显式失败（规格 7 边界路径：不产生半可用对象） */
    @Test
    void emptyChainConstructionFailsExplicitly() {
        assertThrows(IllegalArgumentException.class, () -> LockAide.getReadLock(new ArrayList<>()));
    }

}
