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

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/15 4:07 下午
 */
public interface ObjectLocker<O> {

    Lock lock(O object);

    Lock lockInterruptibly(O object) throws InterruptedException;

    Optional<Lock> tryLock(O object);

    Optional<Lock> tryLock(O object, long timeout, TimeUnit unit) throws InterruptedException;

    void unlock(O object, Lock lock);

}
