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
 * 根治 fix-ci-unit-flakes 本地登记 #2/3/4（2026-10-02 至 2026-10-03 全量并行构建下
 * CollectionLockTest.exclusiveMixturesNeverOverlap 三次偶红）：读读并行驻留的单次观测判据
 * 在调度串行化下会翻转，本轮把"并行驻留未被观测"这一件事改为有界窗口内多轮重试；
 * 安全契约（写持有期间零并发）与不丢任务契约违反仍然立即判红，不重试。
 */
// 类级超时兜底：任何用例若在有界等待之外仍卡住，一分钟后由 JUnit 判红并释放，形态对齐
// EtcdNamespaceExplorerIT 的类级 @Timeout 兜底（stabilize 判例）。
@Timeout(value = 1, unit = TimeUnit.MINUTES, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
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
        // 本用例与 diagnosis.md 本地登记 #2/3/4（2026-10-02 至 2026-10-03 全量并行构建下
        // CollectionLockTest.exclusiveMixturesNeverOverlap 三次偶红）属同一处方族：登记点名的偶红用例
        // 是 exclusiveMixturesNeverOverlap，本用例尚未偶红，但存在同样的"单次观测依赖调度运气"形态——
        // 单轮栅栏会合在负载下可能未达成。本轮把未会合（本方等待超时，或对方超时导致栅栏破裂）判为
        // 弃轮重试而不是判红，直到观测到并行驻留或 20 秒总截止。
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(20);
        int rounds = 0;
        int observedMax = 0;
        Residency parallelRound = null;
        while (parallelRound == null) {
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
                            // 双方都在读区等待会合：读读并行则栅栏达成；本轮未会合则超时或栅栏破裂
                            residency.enter();
                            try {
                                // 栅栏会合成功=两读者同时驻留；上限由 3 秒放宽到 10 秒，
                                // 给负载下迟到的对方留出会合窗口
                                bothWantIn.await(10, TimeUnit.SECONDS);
                            } catch (TimeoutException | BrokenBarrierException e) {
                                // 本轮未会合：本方等待超时，或因对方超时导致栅栏破裂——
                                // 弃本轮换新栅栏重来，不再把破裂直接判红
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
            rounds++;
            observedMax = Math.max(observedMax, residency.max.get());
            if (residency.max.get() >= 2) {
                parallelRound = residency;
            } else if (System.nanoTime() >= deadline) {
                fail("有界窗口 20 秒共 " + rounds + " 轮内读读驻留峰值仅为 " + observedMax
                        + "，始终未观测到两个读者并行驻留，读读并行契约未能验证");
            }
        }
        assertEquals(2, parallelRound.max.get(), "读读必须并行驻留");
    }

    /** 写-写、写-读、读-写交错：任意时刻驻留 1，完成数精确 */
    @Test
    void exclusiveMixturesNeverOverlap() throws Exception {
        List<LockEntity[]> plan = List.of(
                batch(10).toArray(new LockEntity<?>[0]),
                batch(10).toArray(new LockEntity<?>[0]));
        int writers = 2;
        int readers = 2;
        int perThread = 20;
        // 根治 diagnosis.md 本地登记 #2/3/4（2026-10-02 至 2026-10-03 全量并行构建下
        // CollectionLockTest.exclusiveMixturesNeverOverlap 三次偶红）：
        // "读读应可并行驻留"原先是一次性观测，全量并行构建把四个任务排到不同时间片时
        // 观测不到并行、误判互斥过紧。本轮只对"并行驻留未被观测"这一件事在 20 秒总截止内
        // 多轮重试；写持有期间出现并发驻留、任务未全部完成这两条硬契约一旦违反立即判红，
        // 重试会掩盖真病灶，不允许重来。
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(20);
        int rounds = 0;
        int totalCompleted = 0;
        while (true) {
            Residency residency = new Residency();
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
                assertTrue(done.await(10, TimeUnit.SECONDS), "单轮读写交错编排未在 10 秒时限内完成，编排停滞不属于可重试的调度串行化");
            } finally {
                pool.shutdownNow();
            }
            rounds++;
            assertFalse(residency.writeViolation.get(), "写持有期间不得有任何并发驻留（含读者）——硬契约违反，立即判红不重试");
            assertEquals((writers + readers) * perThread, residency.completed.get(), "单轮任务必须全部完成，丢任务属硬契约违反，立即判红不重试");
            totalCompleted += residency.completed.get();
            if (residency.max.get() >= 2) {
                break;
            }
            if (System.nanoTime() >= deadline) {
                fail("有界窗口 20 秒共 " + rounds + " 轮内读读始终未被观测到并行驻留，互斥可能过紧或者调度饥饿过强");
            }
        }
        assertEquals(rounds * (writers + readers) * perThread, totalCompleted);
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
        // 根治 diagnosis.md 本地登记 #2/3/4（2026-10-02 至 2026-10-03 全量并行构建下
        // CollectionLockTest.exclusiveMixturesNeverOverlap 三次偶红）的同族睡眠猜测：
        // 原实现以固定睡眠 150 毫秒赌受害者已经阻塞，负载下受害者可能尚未进入等待队列
        // 就被中断，判定前提落空。文件头约束本就不以睡眠猜测判定，此处改有界轮询线程状态：
        // 受害者被可中断获取阻塞时 park 在等待队列上，线程状态为 WAITING；
        // 5 秒窗口内观测不到 WAITING 即判红
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (victim.getState() != Thread.State.WAITING) {
            if (System.nanoTime() >= deadline) {
                fail("受害者线程未在 5 秒窗口内进入锁等待队列（线程状态始终未变为 WAITING），无法在中断前确认它正阻塞于可中断获取");
            }
            TimeUnit.MILLISECONDS.sleep(10);
        }
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
