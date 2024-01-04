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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 */
public class CounterReentrantLock extends ReentrantLock implements ReleasableLock {

    private static final int DESTROY_STATUS = -1;

    private final AtomicInteger count = new AtomicInteger(0);

    @Override
    public boolean apply(LockerKey key) {
        int value;
        do {
            value = count.get();
            if (value == DESTROY_STATUS) {
                return false;
            }
        } while (!count.compareAndSet(value, value + 1));
        return true;
    }

    @Override
    public void release(LockerKey key) {
        int value;
        int update;
        do {
            value = count.get();
            if (value == 0) {
                return;
            }
            update = value - 1;
            if (update <= DESTROY_STATUS) {
                break;
            }
        } while (!count.compareAndSet(value, update));
    }

    @Override
    public boolean destroy(LockerKey key) {
        return count.compareAndSet(0, DESTROY_STATUS);
    }

    @Override
    public void lock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void lockInterruptibly() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock(long timeout, TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unlock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getApplyCount() {
        return count.get();
    }

    @Override
    public void lock(LockerKey key) {
        super.lock();
    }

    @Override
    public void lockInterruptibly(LockerKey key) throws InterruptedException {
        super.lockInterruptibly();
    }

    @Override
    public boolean tryLock(LockerKey key) {
        return super.tryLock();
    }

    @Override
    public boolean tryLock(LockerKey key, long time, TimeUnit unit) throws InterruptedException {
        return super.tryLock(time, unit);
    }

    @Override
    public void unlock(LockerKey key) {
        super.unlock();
    }

}
