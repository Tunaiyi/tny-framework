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
package com.tny.game.common.concurrent.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.StampedLock;
import java.util.function.*;

/**
 * StampedLock 锁工具。
 * <p>
 * <b>乐观读方法契约</b>：供应商必须幂等/无副作用。执行流程为标准乐观读：
 * 先无锁执行业务，执行后校验期间是否发生写；校验失败则降级悲观读锁重跑业务，
 * 因此业务可能被执行两次（结果一致，开销翻倍）。有副作用的业务（命令入队、IO、
 * 计数）不得使用乐观读方法：读侧请直接从 volatile 字段取快照，写侧使用
 * {@code *InWriteLock} 方法。
 * <p>
 * <b>写锁方法契约</b>：业务在真实写锁互斥下执行，可包含副作用与复合读写。
 *
 * @author : kgtny
 * @date : 2021/5/15 2:43 下午
 */
public final class StampedLockAide {

    private StampedLockAide() {
    }

    /**
     * 在写锁互斥下执行（业务可含副作用）。
     */
    public static void runInWriteLock(StampedLock lock, Runnable runnable) {
        long stamp = lock.writeLock();
        try {
            runnable.run();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * 在写锁互斥下求值（业务可含副作用）。
     */
    public static <T> T supplyInWriteLock(StampedLock lock, Supplier<T> supplier) {
        long stamp = lock.writeLock();
        try {
            return supplier.get();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * 在写锁互斥下调用（业务可含副作用）。
     */
    public static <T> T callInWriteLock(StampedLock lock, Callable<T> caller) throws Exception {
        long stamp = lock.writeLock();
        try {
            return caller.call();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * 以乐观读执行：先无锁执行业务，执行后校验；发生并发写则悲观读锁下重跑一次。
     * <p>
     * 契约：业务必须幂等/无副作用（可能被执行两次），见类文档。
     */
    public static void runInOptimisticReadLock(StampedLock lock, Runnable runnable) {
        long stamp = lock.tryOptimisticRead();
        runnable.run();
        if (!lock.validate(stamp)) { // 执行期间有写入发生：业务结果不可信，悲观读锁下重跑
            stamp = lock.readLock();
            try {
                runnable.run();
            } finally {
                lock.unlockRead(stamp);
            }
        }
    }

    /**
     * 以乐观读求值，语义与失败降级同 {@link #runInOptimisticReadLock}。
     */
    public static <T> T supplyInOptimisticReadLock(StampedLock lock, Supplier<T> supplier) {
        long stamp = lock.tryOptimisticRead();
        T value = supplier.get();
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                value = supplier.get();
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return value;
    }

    /**
     * 以乐观读求值（携带上下文参数），语义同 {@link #supplyInOptimisticReadLock(StampedLock, Supplier)}。
     */
    public static <T, C> T supplyInOptimisticReadLock(StampedLock lock, Function<C, T> function, C context) {
        long stamp = lock.tryOptimisticRead();
        T value = function.apply(context);
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                value = function.apply(context);
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return value;
    }

    /**
     * 以乐观读调用，语义同 {@link #supplyInOptimisticReadLock(StampedLock, Supplier)}，允许受检异常。
     */
    public static <T> T callInOptimisticReadLock(StampedLock lock, Callable<T> caller) throws Exception {
        long stamp = lock.tryOptimisticRead();
        T value = caller.call();
        if (!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                value = caller.call();
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return value;
    }

}
