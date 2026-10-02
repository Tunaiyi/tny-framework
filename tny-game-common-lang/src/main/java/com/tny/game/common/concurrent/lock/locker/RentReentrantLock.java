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

package com.tny.game.common.concurrent.lock.locker;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 */
public class RentReentrantLock extends ReentrantLock implements ReleasableLock {

    private static final int DESTROY_STATUS = -1;

    private final AtomicInteger times = new AtomicInteger(0);

    @Override
    public boolean apply(LockerKey key) {
        int value;
        do {
            value = times.get();
            if (value == DESTROY_STATUS) {
                return false;
            }
        } while (!times.compareAndSet(value, value + 1));
        return true;
    }

    @Override
    public void release(LockerKey key) {
        int value;
        int update;
        do {
            value = times.get();
            if (value == 0) {
                return;
            }
            update = value - 1;
            if (update <= DESTROY_STATUS) {
                break;
            }
        } while (!times.compareAndSet(value, update));
    }

    @Override
    public boolean destroy(LockerKey key) {
        return times.compareAndSet(0, DESTROY_STATUS);
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
        return times.get();
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
