/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.concurrent.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/15 2:43 下午
 */
public final class StampedLockAide {

    private StampedLockAide() {
    }

    public static void runInWriteLock(StampedLock lock, Runnable runnable) {
        long stamp = lock.tryOptimisticRead();
        if (!lock.validate(stamp)) { // 检查乐观读锁后是否有其他写锁发生
            stamp = lock.readLock(); // 获取一个悲观读锁
            try {
                runnable.run();
            } finally {
                lock.unlockRead(stamp); // 释放悲观读锁
            }
        }
        runnable.run();
    }

    public static <T> T supplyInWriteLock(StampedLock lock, Supplier<T> supplier) {
        long stamp = lock.tryOptimisticRead();
        if (!lock.validate(stamp)) { // 检查乐观读锁后是否有其他写锁发生
            stamp = lock.readLock(); // 获取一个悲观读锁
            try {
                return supplier.get();
            } finally {
                lock.unlockRead(stamp); // 释放悲观读锁
            }
        }
        return supplier.get();
    }

    public static <T> T callInWriteLock(StampedLock lock, Callable<T> caller) throws Exception {
        long stamp = lock.tryOptimisticRead();
        if (!lock.validate(stamp)) { // 检查乐观读锁后是否有其他写锁发生
            stamp = lock.readLock(); // 获取一个悲观读锁
            try {
                return caller.call();
            } finally {
                lock.unlockRead(stamp); // 释放悲观读锁
            }
        }
        return caller.call();
    }

    public static void runInOptimisticReadLock(StampedLock lock, Runnable runnable) {
        long stamp = lock.tryOptimisticRead();
        if (!lock.validate(stamp)) { // 检查乐观读锁后是否有其他写锁发生
            stamp = lock.readLock(); // 获取一个悲观读锁
            try {
                runnable.run();
            } finally {
                lock.unlockRead(stamp); // 释放悲观读锁
            }
        }
        runnable.run();
    }

    public static <T> T supplyInOptimisticReadLock(StampedLock lock, Supplier<T> supplier) {
        long stamp = lock.tryOptimisticRead();
        if (!lock.validate(stamp)) { // 检查乐观读锁后是否有其他写锁发生
            stamp = lock.readLock(); // 获取一个悲观读锁
            try {
                return supplier.get();
            } finally {
                lock.unlockRead(stamp); // 释放悲观读锁
            }
        }
        return supplier.get();
    }

    public static <T> T callInOptimisticReadLock(StampedLock lock, Callable<T> caller) throws Exception {
        long stamp = lock.tryOptimisticRead();
        if (!lock.validate(stamp)) { // 检查乐观读锁后是否有其他写锁发生
            stamp = lock.readLock(); // 获取一个悲观读锁
            try {
                return caller.call();
            } finally {
                lock.unlockRead(stamp); // 释放悲观读锁
            }
        }
        return caller.call();
    }

}
