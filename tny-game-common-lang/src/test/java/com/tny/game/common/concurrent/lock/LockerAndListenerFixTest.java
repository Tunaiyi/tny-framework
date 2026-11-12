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
package com.tny.game.common.concurrent.lock;

import com.tny.game.common.concurrent.lock.locker.HashObjectLocker;
import com.tny.game.common.event.GlobalListenerHolder;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 锁具与全局监听器修复契约：
 * HashObjectLocker 的 Math.abs(MIN_VALUE) 负索引越界、
 * GlobalListenerHolder.removeListener 误写 add、
 * LinkedLock 构造条件写反 + lock 失败不回滚。
 * （置于 concurrent.lock 包内：ObjectReadWriteLock 为包私有，桩实现 Comparable 需要访问。）
 */
class LockerAndListenerFixTest {

    /** hashCode 恒为 Integer.MIN_VALUE 的键（原实现在此 AIOOBE） */
    private static final Object MIN_HASH_KEY = new Object() {
        @Override
        public int hashCode() {
            return Integer.MIN_VALUE;
        }
    };

    @Test
    void hashObjectLockerHandlesMinValueHash() {
        HashObjectLocker<Object> locker = new HashObjectLocker<>(16);
        Lock lock = assertDoesNotThrow(() -> locker.lock(MIN_HASH_KEY));
        assertNotNull(lock);
        assertDoesNotThrow(() -> locker.unlock(MIN_HASH_KEY, lock));
        Lock again = locker.lock(MIN_HASH_KEY);
        locker.unlock(MIN_HASH_KEY, again);
    }

    @Test
    void globalListenerHolderRemoveActuallyRemoves() {
        GlobalListenerHolder holder = GlobalListenerHolder.getInstance();
        Runnable listener = () -> {
        };
        holder.addListener(listener);
        assertTrue(holder.getListeners(Runnable.class).contains(listener), "add 后应可查到");
        holder.removeListener(listener);
        assertFalse(holder.getListeners(Runnable.class).contains(listener),
                "remove 后必须移除（原实现误写 add：越删越多）");
    }

    /** LinkedLock：合法列表可构造；原实现条件写反导致完全不可用 */
    @Test
    void linkedLockConstructibleWithRealLocks() {
        List<ObjectLock> locks = new ArrayList<>();
        StubObjectLock s1 = new StubObjectLock();
        StubObjectLock s2 = new StubObjectLock();
        locks.add(s1);
        locks.add(s2);
        LinkedLock chain = assertDoesNotThrow(() -> new LinkedLock(locks));
        chain.lock();
        assertTrue(s1.holder.isHeldByCurrentThread());
        assertTrue(s2.holder.isHeldByCurrentThread());
        chain.unlock();
        assertFalse(s1.holder.isHeldByCurrentThread());
        assertFalse(s2.holder.isHeldByCurrentThread());
        assertThrows(IllegalArgumentException.class, () -> new LinkedLock(new ArrayList<>()));
    }

    /** javadoc 承诺的回滚：后续锁失败时已持有的前锁必须释放（原实现直接重抛，锁泄漏） */
    @Test
    void linkedLockRollsBackOnFailure() {
        StubObjectLock first = new StubObjectLock();
        List<ObjectLock> locks = new ArrayList<>();
        locks.add(first);
        locks.add(new FailLock());
        LinkedLock chain = new LinkedLock(locks);
        assertThrows(RuntimeException.class, chain::lock);
        assertFalse(first.holder.isHeldByCurrentThread(), "后续锁失败必须回滚已持有的前锁");
    }

    private static class StubObjectLock implements ObjectLock {

        private final ReentrantLock holder = new ReentrantLock();

        @Override
        public int compareTo(ObjectReadWriteLock o) {
            return 0;
        }

        @Override
        public void lock() {
            holder.lock();
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            holder.lockInterruptibly();
        }

        @Override
        public boolean tryLock() {
            return holder.tryLock();
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return holder.tryLock(time, unit);
        }

        @Override
        public void unlock() {
            holder.unlock();
        }

        @Override
        public Condition newCondition() {
            return holder.newCondition();
        }

    }

    /** lock 时必然失败的桩（触发回滚路径） */
    private static class FailLock implements ObjectLock {

        @Override
        public int compareTo(ObjectReadWriteLock o) {
            return 0;
        }

        @Override
        public void lock() {
            throw new RuntimeException("模拟后续锁获取失败");
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
        public boolean tryLock(long time, TimeUnit unit) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void unlock() {
        }

        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }

    }

}
