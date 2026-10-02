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
