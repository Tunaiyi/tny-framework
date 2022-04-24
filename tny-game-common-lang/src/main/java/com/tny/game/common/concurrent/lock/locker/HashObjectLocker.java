package com.tny.game.common.concurrent.lock.locker;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

/**
 * <p>
 */
public class HashObjectLocker<O> implements ObjectLocker<O> {

    private final Lock[] locks;

    private final int currentLevel;

    public HashObjectLocker(int currentLevel) {
        this.currentLevel = currentLevel;
        this.locks = new Lock[currentLevel];
        for (int i = 0; i < this.locks.length; i++) {
            this.locks[i] = new ReentrantLock();
        }
    }

    private Lock lockOf(O object) {
        return locks[Math.abs(object.hashCode()) % currentLevel];
    }

    @Override
    public Lock lock(O object) {
        Lock lock = lockOf(object);
        lock.lock();
        return lock;
    }

    @Override
    public Lock lockInterruptibly(O object) throws InterruptedException {
        Lock lock = lockOf(object);
        lock.lockInterruptibly();
        return lock;
    }

    @Override
    public Optional<Lock> tryLock(O object) {
        Lock lock = lockOf(object);
        if (lock.tryLock()) {
            return Optional.of(lock);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Lock> tryLock(O object, long timeout, TimeUnit unit) throws InterruptedException {
        Lock lock = lockOf(object);
        if (lock.tryLock(timeout, unit)) {
            return Optional.of(lock);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void unlock(O object, Lock lock) {
        Lock currentLock = lockOf(object);
        if (currentLock == lock) {
            lock.unlock();
            return;
        }
        throw new IllegalArgumentException("unlock failed, {} lock not match");
    }

    public int size() {
        return currentLevel;
    }

}
