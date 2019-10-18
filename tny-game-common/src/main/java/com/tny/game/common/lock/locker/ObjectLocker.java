package com.tny.game.common.lock.locker;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * <p>
 */
public class ObjectLocker<O> {

    private ConcurrentMap<O, CounterReentrantLock> lockMap = new ConcurrentHashMap<>();

    public Lock lock(O object) {
        CounterReentrantLock lock = lockMap.get(object);
        while (true) {
            if (lock != null) {
                if (lock.apply(LockerKey.KEY)) {
                    lock.lock(LockerKey.KEY);
                    return lock;
                } else {
                    lockMap.remove(object, lock);
                }
            }
            lock = new CounterReentrantLock();
            CounterReentrantLock old = lockMap.putIfAbsent(object, lock);
            if (old != null)
                lock = old;
        }
    }

    public Lock lockInterruptibly(O object) throws InterruptedException {
        CounterReentrantLock lock = lockMap.get(object);
        while (true) {
            if (lock != null) {
                if (lock.apply(LockerKey.KEY)) {
                    try {
                        lock.lockInterruptibly(LockerKey.KEY);
                        return lock;
                    } catch (InterruptedException e) {
                        lock.release(LockerKey.KEY);
                        throw e;
                    }
                } else {
                    lockMap.remove(object, lock);
                }
            }
            lock = new CounterReentrantLock();
            CounterReentrantLock old = lockMap.putIfAbsent(object, lock);
            if (old != null)
                lock = old;
        }
    }

    public Optional<Lock> tryLock(O object) {
        CounterReentrantLock lock = lockMap.get(object);
        while (true) {
            if (lock != null) {
                if (lock.apply(LockerKey.KEY)) {
                    if (lock.tryLock(LockerKey.KEY)) {
                        return Optional.of(lock);
                    } else {
                        lock.release(LockerKey.KEY);
                        return Optional.empty();
                    }
                } else {
                    lockMap.remove(object, lock);
                }
            }
            lock = new CounterReentrantLock();
            CounterReentrantLock old = lockMap.putIfAbsent(object, lock);
            if (old != null)
                lock = old;
        }
    }

    public Optional<Lock> tryLock(O object, long timeout, TimeUnit unit) throws InterruptedException {
        CounterReentrantLock lock = lockMap.get(object);
        while (true) {
            if (lock != null) {
                if (lock.apply(LockerKey.KEY)) {
                    try {
                        if (lock.tryLock(LockerKey.KEY, timeout, unit)) {
                            return Optional.of(lock);
                        } else {
                            lock.release(LockerKey.KEY);
                            return Optional.empty();
                        }
                    } catch (InterruptedException e) {
                        lock.release(LockerKey.KEY);
                        throw e;
                    }
                } else {
                    lockMap.remove(object, lock);
                }
            }
            lock = new CounterReentrantLock();
            CounterReentrantLock old = lockMap.putIfAbsent(object, lock);
            if (old != null)
                lock = old;
        }
    }

    public void unlock(O object, Lock lock) {
        if (lock != null) {
            if (lock instanceof ReleasableLock) {
                ReleasableLock releasableLock = (ReleasableLock) lock;
                try {
                    releasableLock.unlock(LockerKey.KEY);
                } finally {
                    releasableLock.release(LockerKey.KEY);
                    if (releasableLock.destroy(LockerKey.KEY))
                        lockMap.remove(object, releasableLock);
                }
            } else {
                lock.unlock();
            }
        }
    }

    public int size() {
        return lockMap.size();
    }

}
