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

package com.tny.game.common.lock.locker;

import com.tny.game.common.concurrent.lock.locker.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 */
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

    @Test
    void lock() throws ExecutionException, InterruptedException {
        int taskSize = 100;
        CountDownLatch latch = new CountDownLatch(taskSize);
        List<Future<Integer>> futures = new ArrayList<>();
        String lockObject = "locker";
        for (int i = 0; i < taskSize; i++) {
            futures.add(this.service.submit(() -> {
                latch.countDown();
                try {
                    latch.await();
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
            sortedSet.add(future.get());
        }
        assertEquals(taskSize, sortedSet.size());
        int expected = this.initNumber;
        for (int num : sortedSet) {
            assertEquals(++expected, num);
        }
        assertEquals(0, this.locker.size());
    }

    @Test
    void tryLock() throws ExecutionException, InterruptedException {
        int taskSize = 30;
        CountDownLatch latch = new CountDownLatch(taskSize);
        List<Future<Boolean>> futures = new ArrayList<>();
        String lockObject = "locker";
        for (int i = 0; i < taskSize; i++) {
            futures.add(this.service.submit(() -> {
                latch.countDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Optional<Lock> lock = this.locker.tryLock(lockObject);
                try {
                    if (lock.isPresent()) {
                        assertEquals(1, this.locker.size());
                        ++this.number;
                        Thread.sleep(100);
                        return true;
                    } else {
                        return false;
                    }
                } finally {
                    lock.ifPresent(l -> this.locker.unlock(lockObject, l));
                }
            }));
        }
        int lockNum = 0;
        for (Future<Boolean> future : futures) {
            if (future.get()) {
                lockNum++;
            }
        }
        assertEquals(1, lockNum);
        assertEquals(1, this.number);
        assertEquals(0, this.locker.size());
    }

    @Test
    void tryLock1() throws ExecutionException, InterruptedException {
        int taskSize = 30;
        CountDownLatch latch = new CountDownLatch(taskSize);
        List<Future<Boolean>> futures = new ArrayList<>();
        String lockObject = "locker";
        int timeout = 20;
        for (int i = 0; i < taskSize; i++) {
            futures.add(this.service.submit(() -> {
                latch.countDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Optional<Lock> lock = this.locker.tryLock(lockObject, timeout, TimeUnit.MILLISECONDS);
                try {
                    if (lock.isPresent()) {
                        assertEquals(1, this.locker.size());
                        ++this.number;
                        Thread.sleep(timeout + 100);
                        return true;
                    } else {
                        return false;
                    }
                } finally {
                    lock.ifPresent(l -> this.locker.unlock(lockObject, l));
                }
            }));
        }
        int lockNum = 0;
        for (Future<Boolean> future : futures) {
            if (future.get()) {
                lockNum++;
            }
        }
        assertEquals(1, lockNum);
        assertEquals(1, this.number);
        assertEquals(0, this.locker.size());
    }

    @Test
    void lockInterruptibly() throws InterruptedException {
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
                    latch.await();
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
                sortedSet.add(future.get());
            } catch (ExecutionException e) {
                if (e.getCause() instanceof InterruptedException) {
                    interruptedNum++;
                }
            }
        }
        assertEquals(taskSize - interruptedSize, sortedSet.size(), "" + this.locker);
        assertEquals(interruptedSize, interruptedNum, "" + this.locker);
        int expected = this.initNumber;
        for (int num : sortedSet) {
            assertEquals(++expected, num);
        }
        assertEquals(0, this.locker.size(), "" + this.locker);
    }

}