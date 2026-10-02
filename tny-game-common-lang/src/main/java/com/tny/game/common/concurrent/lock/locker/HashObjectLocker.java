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
        // Math.abs(Integer.MIN_VALUE) 仍为负 → 负索引越界；floorMod 天然非负
        return locks[Math.floorMod(object.hashCode(), currentLevel)];
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
