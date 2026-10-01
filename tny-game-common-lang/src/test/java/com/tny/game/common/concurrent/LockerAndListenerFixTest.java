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
package com.tny.game.common.concurrent;

import com.tny.game.common.concurrent.lock.locker.HashObjectLocker;
import com.tny.game.common.event.GlobalListenerHolder;
import org.junit.jupiter.api.*;

import java.util.concurrent.locks.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 锁具与全局监听器修复契约：
 * HashObjectLocker 的 Math.abs(MIN_VALUE) 负索引越界、
 * GlobalListenerHolder.removeListener 误写 add。
 * LinkedLock 相关契约测试见 concurrent.lock 包同名文件（ObjectReadWriteLock 为包私有）。
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

}
