package com.tny.game.common.concurrent.lock.locker;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

/**
 * <p>
 */
public class MapObjectLocker<O> implements ObjectLocker<O> {

    private final ConcurrentMap<O, ReleasableLock> lockMap = new ConcurrentHashMap<>();

    @Override
    public Lock lock(O object) {
        ReleasableLock lock = lockMap.get(object);
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
            ReleasableLock old = lockMap.putIfAbsent(object, lock);
            if (old != null) {
                lock = old;
            }
        }
    }

    @Override
    public Lock lockInterruptibly(O object) throws InterruptedException {
        ReleasableLock lock = lockMap.get(object);
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
            ReleasableLock old = lockMap.putIfAbsent(object, lock);
            if (old != null) {
                lock = old;
            }
        }
    }

    @Override
    public Optional<Lock> tryLock(O object) {
        ReleasableLock lock = lockMap.get(object);
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
            ReleasableLock old = lockMap.putIfAbsent(object, lock);
            if (old != null) {
                lock = old;
            }
        }
    }

    @Override
    public Optional<Lock> tryLock(O object, long timeout, TimeUnit unit) throws InterruptedException {
        ReleasableLock lock = lockMap.get(object);
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
            ReleasableLock old = lockMap.putIfAbsent(object, lock);
            if (old != null) {
                lock = old;
            }
        }
    }

    @Override
    public void unlock(O object, Lock lock) {
        if (lock != null) {
            if (lock instanceof ReleasableLock) {
                ReleasableLock releasableLock = (ReleasableLock)lock;
                try {
                    releasableLock.unlock(LockerKey.KEY);
                } finally {
                    releasableLock.release(LockerKey.KEY);
                    if (releasableLock.destroy(LockerKey.KEY)) {
                        lockMap.remove(object, releasableLock);
                    }
                }
            } else {
                lock.unlock();
            }
        }
    }

    public int size() {
        return lockMap.size();
    }

    @Override
    public String toString() {
        return "MapObjectLocker{" + "lockMap=" + lockMap + '}';
    }

}
