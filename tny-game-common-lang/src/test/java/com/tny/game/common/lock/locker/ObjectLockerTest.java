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

package com.tny.game.common.lock.locker;

import com.tny.game.common.concurrent.lock.locker.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 * 根治 fix-ci-unit-flakes 案 #2（CI unit 工作流 GitHub Actions 运行号 run id 37165077819，2026-10-04）：
 * 末尾的锁映射状态观测断言与"恰好一个线程获取"的会合判定原先依赖固定睡眠窗口，在 CI 负载下翻转。
 * 本轮改造把所有启动栅栏与结果收集改为带时限的有界等待、把固定持有时长改会合形态、把末尾单次观测改有界轮询，
 * 并配合 MapperLocker 生产修复（获取失败与中断路径归还引用计数后销毁并回收条目）。
 */
// 类级超时兜底：任何用例若在有界等待之外仍卡住，一分钟后由 JUnit 判红并释放，形态对齐
// EtcdNamespaceExplorerIT 的类级 @Timeout 兜底（stabilize 判例）。
@Timeout(value = 1, unit = TimeUnit.MINUTES, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
class ObjectLockerTest {

    private volatile MapperLocker<Object> locker;

    private final ExecutorService service = Executors.newCachedThreadPool();

    private final int initNumber = 0;

    private int number = 0;

    @BeforeEach
    void setUp() {
        this.locker = MapperLocker.common();
        this.number = this.initNumber;
    }

    @AfterEach
    void tearDown() {
        // 防止用例内线程池任务残留：整池即刻停止，避免线程泄漏到后续用例
        this.service.shutdownNow();
    }

    /**
     * 有界等待锁映射归还为空：约 20 毫秒轮询一次，5 秒截止仍非空即判红。
     * 本断言的稳定性依赖本轮 MapperLocker 生产修复（diagnosis.md 案 #2，CI unit 工作流
     * GitHub Actions 运行号 run id 37165077819，2026-10-04）：修复前获取失败与被中断的
     * 路径只归还引用计数而不销毁条目，最后一个把计数归零的释放者恰好走这两条路径时，
     * 映射表永久残留一个零引用条目，任何有界轮询都收敛不到空；修复后这两条路径与
     * unlock 一样完成销毁与移除，轮询必然收敛。
     */
    private void assertLockerEmptied() throws InterruptedException {
        long deadline = System.currentTimeMillis() + 5_000L;
        while (this.locker.size() > 0) {
            if (System.currentTimeMillis() >= deadline) {
                fail("锁映射未在 5 秒窗口内归还为空，当前观测：" + this.locker);
            }
            TimeUnit.MILLISECONDS.sleep(20);
        }
    }

    @Test
    void lock() throws ExecutionException, InterruptedException, TimeoutException {
        int taskSize = 100;
        CountDownLatch latch = new CountDownLatch(taskSize);
        List<Future<Integer>> futures = new ArrayList<>();
        String lockObject = "locker";
        for (int i = 0; i < taskSize; i++) {
            futures.add(this.service.submit(() -> {
                latch.countDown();
                try {
                    // 启动栅栏只尽力制造并发竞争窗口，超时后任务照常继续，不影响被测的互斥性质
                    latch.await(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Lock lock = this.locker.lock(lockObject);
                try {
                    assertTrue(this.locker.size() > 0);
                    return ++this.number;
                } finally {
                    this.locker.unlock(lockObject, lock);
                }
            }));
        }
        SortedSet<Integer> sortedSet = new TreeSet<>();
        for (Future<Integer> future : futures) {
            // 结果收集同样带显式时限（判例要求全部阻塞等待有界）
            sortedSet.add(future.get(30, TimeUnit.SECONDS));
        }
        assertEquals(taskSize, sortedSet.size());
        int expected = this.initNumber;
        for (int num : sortedSet) {
            assertEquals(++expected, num);
        }
        // 本用例全部线程都经 unlock 的销毁回收归还条目，末尾观测本来就是确定语义；
        // 改用同一有界轮询辅助方法仅为形态统一
        assertLockerEmptied();
    }

    @Test
    void tryLock() throws ExecutionException, InterruptedException, TimeoutException {
        int taskSize = 30;
        CountDownLatch latch = new CountDownLatch(taskSize);
        // 会合形态（根治案 #2，CI unit 工作流运行号 run id 37165077819，2026-10-04）：
        // 原先固定持有 100 毫秒靠"全部尝试挤进同一窗口"的运气才会出现"恰好一个获取"，
        // 负载下尝试落到窗口之外就会多出一个获取者。现在持有者等到其余线程都完成失败
        // 尝试后才释放，"恰好一个线程获取"不再依赖窗口运气。
        CountDownLatch attempted = new CountDownLatch(taskSize - 1);
        List<Future<Boolean>> futures = new ArrayList<>();
        String lockObject = "locker";
        for (int i = 0; i < taskSize; i++) {
            futures.add(this.service.submit(() -> {
                latch.countDown();
                try {
                    // 启动栅栏只尽力制造并发竞争窗口，超时后任务照常继续，不影响被测的互斥性质
                    latch.await(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Optional<Lock> lock = this.locker.tryLock(lockObject);
                try {
                    if (lock.isPresent()) {
                        assertEquals(1, this.locker.size());
                        ++this.number;
                        // 有界会合：其余线程全部完成失败尝试后持有者才进入释放；
                        // 超时也照常进入释放，不挂死
                        attempted.await(30, TimeUnit.SECONDS);
                        return true;
                    } else {
                        attempted.countDown();
                        return false;
                    }
                } finally {
                    lock.ifPresent(l -> this.locker.unlock(lockObject, l));
                }
            }));
        }
        int lockNum = 0;
        for (Future<Boolean> future : futures) {
            if (future.get(30, TimeUnit.SECONDS)) {
                lockNum++;
            }
        }
        assertEquals(1, lockNum);
        assertEquals(1, this.number);
        assertLockerEmptied();
    }

    @Test
    void tryLock1() throws ExecutionException, InterruptedException, TimeoutException {
        int taskSize = 30;
        CountDownLatch latch = new CountDownLatch(taskSize);
        // 会合形态，与 tryLock 用例同理（根治案 #2，CI unit 工作流运行号 run id 37165077819，2026-10-04）：
        // 删除固定持有时长，持有者等其余线程都完成限时尝试并失败之后才释放。
        // 其余线程各自至多等 20 毫秒即失败返回，不会与持有者互相僵等。
        CountDownLatch attempted = new CountDownLatch(taskSize - 1);
        List<Future<Boolean>> futures = new ArrayList<>();
        String lockObject = "locker";
        int timeout = 20;
        for (int i = 0; i < taskSize; i++) {
            futures.add(this.service.submit(() -> {
                latch.countDown();
                try {
                    // 启动栅栏只尽力制造并发竞争窗口，超时后任务照常继续，不影响被测的互斥性质
                    latch.await(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Optional<Lock> lock = this.locker.tryLock(lockObject, timeout, TimeUnit.MILLISECONDS);
                try {
                    if (lock.isPresent()) {
                        assertEquals(1, this.locker.size());
                        ++this.number;
                        // 有界会合：其余线程全部完成失败尝试后持有者才进入释放；
                        // 超时也照常进入释放，不挂死
                        attempted.await(30, TimeUnit.SECONDS);
                        return true;
                    } else {
                        attempted.countDown();
                        return false;
                    }
                } finally {
                    lock.ifPresent(l -> this.locker.unlock(lockObject, l));
                }
            }));
        }
        int lockNum = 0;
        for (Future<Boolean> future : futures) {
            if (future.get(30, TimeUnit.SECONDS)) {
                lockNum++;
            }
        }
        assertEquals(1, lockNum);
        assertEquals(1, this.number);
        assertLockerEmptied();
    }

    @Test
    void lockInterruptibly() throws InterruptedException, TimeoutException {
        int taskSize = 30;
        int interruptedSize = 30 / 2;
        CountDownLatch latch = new CountDownLatch(taskSize);
        List<Future<Integer>> futures = new ArrayList<>();
        String lockObject = "locker";
        for (int i = 0; i < taskSize; i++) {
            int taskNum = i;
            futures.add(this.service.submit(() -> {
                latch.countDown();
                try {
                    // 启动栅栏只尽力制造并发竞争窗口，超时后任务照常继续，不影响被测的互斥性质
                    latch.await(30, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (taskNum < interruptedSize) {
                    Thread.currentThread().interrupt();
                }
                Lock lock = null;
                try {
                    lock = this.locker.lockInterruptibly(lockObject);
                    assertEquals(1, this.locker.size());
                    return ++this.number;
                } finally {
                    if (lock != null) {
                        this.locker.unlock(lockObject, lock);
                    }
                }
            }));
        }
        SortedSet<Integer> sortedSet = new TreeSet<>();
        int interruptedNum = 0;
        for (Future<Integer> future : futures) {
            try {
                sortedSet.add(future.get(30, TimeUnit.SECONDS));
            } catch (ExecutionException e) {
                if (e.getCause() instanceof InterruptedException) {
                    interruptedNum++;
                }
            }
        }
        // 两个严格计数断言在负载下确定的依据：RentReentrantLock 继承 ReentrantLock，
        // lockInterruptibly 经由 AbstractQueuedSynchronizer.acquireInterruptibly 在入口先检查
        // 中断标志，预先被中断的线程无论锁是否空闲都必然抛 InterruptedException，计数因此确定。
        // 本案卷旧翻转来自末尾状态观测：中断路径归还引用计数后不销毁条目，映射表残留零引用
        // 条目（diagnosis.md 案 #2，CI unit 工作流 GitHub Actions 运行号 run id 37165077819，2026-10-04），
        // 由本轮 MapperLocker 生产修复消除残留后收敛。
        assertEquals(taskSize - interruptedSize, sortedSet.size(), "" + this.locker);
        assertEquals(interruptedSize, interruptedNum, "" + this.locker);
        int expected = this.initNumber;
        for (int num : sortedSet) {
            assertEquals(++expected, num);
        }
        assertLockerEmptied();
    }

}
