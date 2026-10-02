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
package com.tny.game.common.concurrent.lock;

import com.tny.game.common.concurrent.exception.LockTimeOutException;
import org.junit.jupiter.api.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * lock-facilities 契约（fix-common-dormant-defects 组2）：
 * 批量读锁真实读语义（读读并行）、混合批只升写不降读、锁池过期重建原子（同标识单实例）、
 * 失效句柄显式失败、锁链回滚与中断标志恢复。置于被测包内以行使包私有构造（短租约注入）。
 */
class LockFacilitiesContractTest {

    static class IntEntity implements LockEntity<Integer> {
        final int id;

        IntEntity(int id) {
            this.id = id;
        }

        @Override
        public Integer getIdentity() {
            return id;
        }
    }

    @SuppressWarnings("unchecked")
    private static Collection<LockEntity<?>> entities(int... ids) {
        List<LockEntity<?>> list = new ArrayList<>();
        for (int id : ids) {
            list.add(new IntEntity(id));
        }
        return list;
    }

    // ---- 批量读锁真实为读（规格 1）----

    /** 双线程批量读锁可并行驻留（原实拿写锁→互斥，读读并行观测必为 1） */
    @Test
    void batchReadLocksAreParallel() throws Exception {
        Collection<LockEntity<?>> batch = entities(1, 2);
        AtomicInteger resident = new AtomicInteger();
        AtomicInteger maxResident = new AtomicInteger();
        CountDownLatch bothIn = new CountDownLatch(2);
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int t = 0; t < 2; t++) {
                futures.add(pool.submit(() -> {
                    Lock lock = LockAide.getReadLock(batch);
                    lock.lock();
                    try {
                        maxResident.accumulateAndGet(resident.incrementAndGet(), Math::max);
                        bothIn.countDown();
                        // 等待对侧也进入（读读并行应可达成；被互斥则超时退出）
                        bothIn.await(2, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        resident.decrementAndGet();
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
        assertEquals(2, maxResident.get(), "批量读锁退化为互斥（读读不并行）");
    }

    /** 读持有期间写被挡（读写互斥） */
    @Test
    void writeBlockedByRead() throws Exception {
        Collection<LockEntity<?>> batch = entities(7);
        CountDownLatch inRead = new CountDownLatch(1);
        CountDownLatch releaseRead = new CountDownLatch(1);
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            Future<?> reader = pool.submit(() -> {
                Lock read = LockAide.getReadLock(batch);
                read.lock();
                try {
                    inRead.countDown();
                    awaitQuiet(releaseRead, 5);
                } finally {
                    read.unlock();
                }
            });
            assertTrue(inRead.await(5, TimeUnit.SECONDS));
            Lock write = LockAide.getWriteLock(new IntEntity(7));
            assertFalse(write.tryLock(), "读持有期间写获取必须被挡");
            releaseRead.countDown();
            reader.get(5, TimeUnit.SECONDS);
            assertTrue(write.tryLock(2, TimeUnit.SECONDS), "读释放后写应可获取");
            write.unlock();
        } finally {
            pool.shutdownNow();
        }
    }

    // ---- 混合批只升写不降读（规格 2）----

    /** 同实体读写同批：持有必须为写语义——他线程读获取被挡（原实现被 READ 覆写降读） */
    @Test
    void mixedBatchNeverDowngradesToRead() throws Exception {
        IntEntity shared = new IntEntity(42);
        Lock mixed = LockAide.getLock(entities(42), entities(42)); // 读集与写集同含该实体
        mixed.lock();
        try {
            // 必须真实他线程探测：同线程读写锁存在重入语义，不构成交互证明
            ExecutorService probePool = Executors.newSingleThreadExecutor();
            try {
                Boolean otherReadAcquired = probePool.submit(() -> {
                    Lock probe = LockAide.getReadLock(entities(42));
                    boolean got = probe.tryLock();
                    if (got) {
                        probe.unlock();
                    }
                    return got;
                }).get(10, TimeUnit.SECONDS);
                assertFalse(otherReadAcquired,
                        "混合批持有被降格为读——他线程读锁并行进入，违反写独占");
            } finally {
                probePool.shutdownNow();
            }
        } finally {
            mixed.unlock();
        }
        // 释放干净：后续读/写正常获取无非法监视器状态
        Lock followUpRead = LockAide.getReadLock(entities(42));
        assertTrue(followUpRead.tryLock(2, TimeUnit.SECONDS));
        followUpRead.unlock();
        Lock followUpWrite = LockAide.getWriteLock(new IntEntity(42));
        assertTrue(followUpWrite.tryLock(2, TimeUnit.SECONDS));
        followUpWrite.unlock();
    }

    // ---- 锁池过期重建原子（规格 3）----

    /** 过期条目并发重建：全部获取者同一实例、写临界区驻留恒 1（原实现分叉互斥破） */
    @Test
    void expiredRebuildYieldsSingleInstance() throws Exception {
        ObjectLockHolder holder = new ObjectLockHolder();
        IntEntity entity = new IntEntity(1234);
        // 种入 1ms 短租约条目，令其必然过期触发重建
        ObjectReadWriteLock stale = new ObjectReadWriteLock(entity, false, 1L);
        injectHolderEntry(holder, IntEntity.class, entity.getIdentity(), stale);
        Thread.sleep(5);

        int threads = 8;
        CyclicBarrier barrier = new CyclicBarrier(threads);
        Set<ObjectReadWriteLock> results = Collections.synchronizedSet(new HashSet<>());
        AtomicInteger resident = new AtomicInteger();
        AtomicInteger maxResident = new AtomicInteger();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        List<Future<?>> futures = new ArrayList<>();
        try {
            for (int t = 0; t < threads; t++) {
                futures.add(pool.submit(() -> {
                    try {
                        barrier.await(5, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    ObjectReadWriteLock lock = holder.getLock(entity, LockType.WRITE);
                    results.add(lock);
                    lock.lock();
                    try {
                        maxResident.accumulateAndGet(resident.incrementAndGet(), Math::max);
                        Thread.yield();
                        maxResident.accumulateAndGet(resident.get(), Math::max);
                    } finally {
                        resident.decrementAndGet();
                        lock.unlock();
                    }
                    return null;
                }));
            }
            for (Future<?> f : futures) {
                f.get(10, TimeUnit.SECONDS);
            }
        } finally {
            pool.shutdownNow();
        }
        assertEquals(1, results.size(), "并发重建产生多把实例（同标识分叉）");
        assertEquals(1, maxResident.get(), "重建分叉导致写写并发进入临界区");
    }

    /** 未过期条目复用不重建（正常路径） */
    @Test
    void freshEntryReusedWithoutRebuild() {
        ObjectLockHolder holder = new ObjectLockHolder();
        IntEntity entity = new IntEntity(555);
        ObjectReadWriteLock first = holder.getLock(entity, LockType.READ);
        ObjectReadWriteLock second = holder.getLock(entity, LockType.READ);
        assertSame(first, second, "未过期条目每次获取都触发重建");
    }

    // ---- 失效句柄上锁显式失败（规格 4 错误路径）----

    @Test
    void deadHandleLockFailsExplicitly() throws Exception {
        ObjectReadWriteLock shortLease = new ObjectReadWriteLock(new IntEntity(777), false, 1L);
        shortLease.beGot(LockType.READ);
        Thread.sleep(5);
        assertThrows(LockTimeOutException.class, shortLease::lock, "失效句柄不得静默放行");
        assertThrows(LockTimeOutException.class, shortLease::tryLock);
        assertThrows(LockTimeOutException.class,
                () -> shortLease.tryLock(10, TimeUnit.MILLISECONDS));
    }

    /** 双线程高频续期在有效期内全部成功（规格 4 正常路径） */
    @Test
    void concurrentRenewalsAllSucceed() throws Exception {
        ObjectReadWriteLock lock = new ObjectReadWriteLock(new IntEntity(888), false, 60_000L);
        CountDownLatch start = new CountDownLatch(1);
        AtomicBoolean anyFailed = new AtomicBoolean();
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int t = 0; t < 2; t++) {
                futures.add(pool.submit(() -> {
                    try {
                        start.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    for (int i = 0; i < 3000; i++) {
                        if (!lock.beGot(i % 2 == 0 ? LockType.READ : LockType.WRITE)) {
                            anyFailed.set(true);
                        }
                    }
                }));
            }
            start.countDown();
            for (Future<?> f : futures) {
                f.get(10, TimeUnit.SECONDS);
            }
        } finally {
            pool.shutdownNow();
        }
        assertFalse(anyFailed.get(), "有效期内续期出现误判失败");
    }

    // ---- 锁链回滚与中断（规格 7）----

    /** 限时获取中途超时：此前已获取的锁全部释放（已有回滚，钉桩） */
    @Test
    void chainTimeoutRollsBackAll() throws Exception {
        ControllableLock first = new ControllableLock();
        ControllableLock secondHeld = new ControllableLock();
        secondHeld.forceHeldByOther();
        LinkedLock chain = new LinkedLock(new ArrayList<ObjectLock>(List.of(first, secondHeld)));
        assertFalse(chain.tryLock(80, TimeUnit.MILLISECONDS), "限时获取应失败");
        assertTrue(first.heldByNobody(), "超时后首把锁未回滚释放");
    }

    /** 等待中被中断：回滚 + 中断标志保持置位 */
    @Test
    void chainInterruptRollsBackAndKeepsFlag() throws Exception {
        ControllableLock first = new ControllableLock();
        ControllableLock blocked = new ControllableLock();
        blocked.forceHeldByOther();
        LinkedLock chain = new LinkedLock(new ArrayList<ObjectLock>(List.of(first, blocked)));
        AtomicReference<Throwable> thrown = new AtomicReference<>();
        AtomicReference<Boolean> interruptFlagAfter = new AtomicReference<>();
        Thread waiter = new Thread(() -> {
            try {
                chain.lockInterruptibly();
            } catch (Throwable e) {
                thrown.set(e);
                interruptFlagAfter.set(Thread.currentThread().isInterrupted());
            }
        });
        waiter.start();
        Thread.sleep(150);
        waiter.interrupt();
        waiter.join(5000);
        assertInstanceOf(InterruptedException.class, thrown.get());
        assertTrue(Boolean.TRUE.equals(interruptFlagAfter.get()),
                "中断后必须恢复中断标志（被异常吞失=调用方无法感知取消）");
        assertTrue(first.heldByNobody(), "中断后首把锁未回滚");
    }

    /** 空集合建链显式失败；单元素/多元素构造可用（首轮修复钉桩） */
    @Test
    void chainConstructionContract() {
        assertThrows(IllegalArgumentException.class,
                () -> new LinkedLock(new ArrayList<>()));
        ControllableLock one = new ControllableLock();
        LinkedLock single = new LinkedLock(new ArrayList<ObjectLock>(List.of(one)));
        assertEquals(1, single.size());
        single.lock();
        assertFalse(one.heldByNobody());
        single.unlock();
        assertTrue(one.heldByNobody());
    }

    // ---- 反死锁排序（规格 1）----

    /** 反序提交两批写锁多轮：固定内部排序 → 零死锁 */
    @Test
    void reversedBatchesNoDeadlock() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            List<Future<?>> futures = new ArrayList<>();
            for (int t = 0; t < 2; t++) {
                final int[] order = t == 0 ? new int[]{101, 102, 103} : new int[]{103, 102, 101};
                futures.add(pool.submit(() -> {
                    for (int round = 0; round < 50; round++) {
                        Lock lock = LockAide.getWriteLock(new IntEntity(order[0]),
                                new IntEntity(order[1]), new IntEntity(order[2]));
                        lock.lock();
                        lock.unlock();
                    }
                }));
            }
            for (Future<?> f : futures) {
                f.get(15, TimeUnit.SECONDS);
            }
        } finally {
            pool.shutdownNow();
        }
    }

    /** 批内空项被忽略（规格 1 错误路径） */
    @Test
    void batchIgnoresNullEntities() {
        List<LockEntity<?>> withNull = new ArrayList<>(List.of(new IntEntity(1)));
        withNull.add(null);
        Lock lock = LockAide.getWriteLock(withNull.toArray(new LockEntity<?>[0]));
        assertDoesNotThrow(() -> {
            lock.lock();
            lock.unlock();
        });
    }

    // ---- helpers ----

    private static void awaitQuiet(CountDownLatch latch, int seconds) {
        try {
            latch.await(seconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void injectHolderEntry(ObjectLockHolder holder, Class<?> clazz,
                                           Comparable<?> identity, ObjectReadWriteLock lock) throws Exception {
        Method getHolder = ObjectLockHolder.class.getDeclaredMethod("getHolder", Class.class);
        getHolder.setAccessible(true);
        Object holderForClass = getHolder.invoke(holder, clazz);
        Field locksField = ObjectLockHolder.Holder.class.getDeclaredField("locks");
        locksField.setAccessible(true);
        ((ConcurrentMap<Object, Object>) locksField.get(holderForClass)).put(identity, lock);
    }

    /** 可控 ObjectLock 桩：包装 ReentrantLock，可令"他线程持有"以驱动失败路径 */
    static class ControllableLock implements ObjectLock {

        private final ReentrantLock inner = new ReentrantLock();

        private volatile boolean foreignHold;

        void forceHeldByOther() throws Exception {
            foreignHold = true;
            CountDownLatch acquired = new CountDownLatch(1);
            Thread holder = new Thread(() -> {
                inner.lock();
                acquired.countDown();
                try {
                    while (foreignHold) {
                        Thread.sleep(20);
                    }
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                } finally {
                    inner.unlock();
                }
            });
            holder.setDaemon(true);
            holder.start();
            assertTrue(acquired.await(5, TimeUnit.SECONDS));
        }

        void releaseForeign() {
            foreignHold = false;
        }

        boolean heldByNobody() {
            return !inner.isLocked();
        }

        @Override
        public int compareTo(ObjectReadWriteLock o) {
            return 0;
        }

        @Override
        public void lock() {
            inner.lock();
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            inner.lockInterruptibly();
        }

        @Override
        public boolean tryLock() {
            return inner.tryLock();
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return inner.tryLock(time, unit);
        }

        @Override
        public void unlock() {
            inner.unlock();
        }

        @Override
        public Condition newCondition() {
            return inner.newCondition();
        }
    }

}
