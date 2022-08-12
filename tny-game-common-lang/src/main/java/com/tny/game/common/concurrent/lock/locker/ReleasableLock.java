/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.concurrent.lock.locker;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * <p>
 */
public interface ReleasableLock extends Lock {

    /**
     * 申请
     *
     * @param key 调用钥匙
     * @return 是否申请到
     */
    boolean apply(LockerKey key);

    /**
     * @return 获取申请数
     */
    int getApplyCount();

    /**
     * 释放
     *
     * @param key 调用钥匙, 防止外部调用
     */
    void release(LockerKey key);

    /**
     * 销毁
     *
     * @param key 调用钥匙, 防止外部调用
     * @return 是否销毁成功
     */
    boolean destroy(LockerKey key);

    /**
     * @param key 调用钥匙
     * @see java.util.concurrent.locks.Lock void lock()
     */
    void lock(LockerKey key);

    /**
     * @param key 调用钥匙
     * @throws InterruptedException 打断异常
     * @see java.util.concurrent.locks.Lock void lockInterruptibly() throws InterruptedException
     */
    void lockInterruptibly(LockerKey key) throws InterruptedException;

    /**
     * @param key 调用钥匙
     * @return 返回是否获取锁
     * @see java.util.concurrent.locks.Lock boolean tryLock()
     */
    boolean tryLock(LockerKey key);

    /**
     * @param key  调用钥匙
     * @param time 超时时间
     * @param unit 超时时间单位
     * @return 返回是否获取锁
     * @throws InterruptedException 打断异常
     * @see java.util.concurrent.locks.Lock boolean tryLock(long time, TimeUnit unit) throws InterruptedException
     */
    boolean tryLock(LockerKey key, long time, TimeUnit unit) throws InterruptedException;

    /**
     * @param key 调用钥匙
     * @see java.util.concurrent.locks.Lock void unlock(LockerKey key)
     */
    void unlock(LockerKey key);

}
