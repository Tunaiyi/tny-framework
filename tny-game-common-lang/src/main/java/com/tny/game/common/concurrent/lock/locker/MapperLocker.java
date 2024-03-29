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

package com.tny.game.common.concurrent.lock.locker;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public class MapperLocker<O> implements ObjectLocker<O> {

    // TODO 内存泄露监控
    private static final MapperLocker<Object> defaultLocker = new MapperLocker<>();

    public static <O> MapperLocker<O> common() {
        return as(defaultLocker);
    }

    private final ConcurrentMap<O, ReleasableLock> lockMap = new ConcurrentHashMap<>();

    private MapperLocker() {
    }

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
            lock = new RentReentrantLock();
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
            lock = new RentReentrantLock();
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
            lock = new RentReentrantLock();
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
            lock = new RentReentrantLock();
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
                ReleasableLock releasableLock = (ReleasableLock) lock;
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
