package com.tny.game.common.lock.locker;

import com.tny.game.common.concurrent.lock.locker.*;
import org.junit.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <p>
 */
public class ObjectLockerTest {

    private ObjectLocker<Object> locker;

    private ExecutorService service = Executors.newCachedThreadPool();

    private int initNumber = 0;
    private int number = 0;

    @Before
    public void setUp() {
        this.locker = new ObjectLocker<>();
        this.number = initNumber;
    }

    @Test
    public void lock() throws ExecutionException, InterruptedException {
        int taskSize = 100;
        CountDownLatch latch = new CountDownLatch(taskSize);
        List<Future<Integer>> futures = new ArrayList<>();
        String lockObject = "locker";
        for (int i = 0; i < taskSize; i++) {
            futures.add(service.submit(() -> {
                latch.countDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Lock lock = locker.lock(lockObject);
                try {
                    assertTrue(locker.size() > 0);
                    return ++number;
                } finally {
                    locker.unlock(lockObject, lock);
                }
            }));
        }
        SortedSet<Integer> sortedSet = new TreeSet<>();
        for (Future<Integer> future : futures) {
            sortedSet.add(future.get());
        }
        assertEquals(taskSize, sortedSet.size());
        int expected = initNumber;
        for (int num : sortedSet) {
            assertEquals(++expected, num);
        }
        assertEquals(0, locker.size());
    }

    @Test
    public void tryLock() throws ExecutionException, InterruptedException {
        int taskSize = 30;
        CountDownLatch latch = new CountDownLatch(taskSize);
        List<Future<Boolean>> futures = new ArrayList<>();
        String lockObject = "locker";
        for (int i = 0; i < taskSize; i++) {
            futures.add(service.submit(() -> {
                latch.countDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Optional<Lock> lock = locker.tryLock(lockObject);
                try {
                    if (lock.isPresent()) {
                        assertEquals(1, locker.size());
                        ++number;
                        Thread.sleep(100);
                        return true;
                    } else {
                        return false;
                    }
                } finally {
                    lock.ifPresent(l -> locker.unlock(lockObject, l));
                }
            }));
        }
        int lockNum = 0;
        for (Future<Boolean> future : futures) {
            if (future.get())
                lockNum++;
        }
        assertEquals(1, lockNum);
        assertEquals(1, number);
        assertEquals(0, locker.size());
    }

    @Test
    public void tryLock1() throws ExecutionException, InterruptedException {
        int taskSize = 30;
        CountDownLatch latch = new CountDownLatch(taskSize);
        List<Future<Boolean>> futures = new ArrayList<>();
        String lockObject = "locker";
        int timeout = 20;
        for (int i = 0; i < taskSize; i++) {
            futures.add(service.submit(() -> {
                latch.countDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Optional<Lock> lock = locker.tryLock(lockObject, timeout, TimeUnit.MILLISECONDS);
                try {
                    if (lock.isPresent()) {
                        assertEquals(1, locker.size());
                        ++number;
                        Thread.sleep(timeout + 100);
                        return true;
                    } else {
                        return false;
                    }
                } finally {
                    lock.ifPresent(l -> locker.unlock(lockObject, l));
                }
            }));
        }
        int lockNum = 0;
        for (Future<Boolean> future : futures) {
            if (future.get())
                lockNum++;
        }
        assertEquals(1, lockNum);
        assertEquals(1, number);
        assertEquals(0, locker.size());
    }

    @Test
    public void lockInterruptibly() throws InterruptedException {
        int taskSize = 30;
        int interruptedSize = 30 / 2;
        CountDownLatch latch = new CountDownLatch(taskSize);
        List<Future<Integer>> futures = new ArrayList<>();
        String lockObject = "locker";
        for (int i = 0; i < taskSize; i++) {
            int taskNum = i;
            futures.add(service.submit(() -> {
                latch.countDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (taskNum < interruptedSize)
                    Thread.currentThread().interrupt();
                Lock lock = null;
                try {
                    lock = locker.lockInterruptibly(lockObject);
                    assertEquals(1, locker.size());
                    return ++number;
                } finally {
                    if (lock != null)
                        locker.unlock(lockObject, lock);
                }
            }));
        }
        SortedSet<Integer> sortedSet = new TreeSet<>();
        int interruptedNum = 0;
        for (Future<Integer> future : futures) {
            try {
                sortedSet.add(future.get());
            } catch (ExecutionException e) {
                if (e.getCause() instanceof InterruptedException)
                    interruptedNum++;
            }
        }
        assertEquals(taskSize - interruptedSize, sortedSet.size());
        assertEquals(interruptedSize, interruptedNum);
        int expected = initNumber;
        for (int num : sortedSet) {
            assertEquals(++expected, num);
        }
        assertEquals(0, locker.size());
    }

}