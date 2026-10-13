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
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public class MapperLocker<O> implements ObjectLocker<O> {

    // TODO 内存泄露监控
    private static final MapperLocker<Object> defaultLocker = new MapperLocker<>();

    public static <O> MapperLocker<O> common() {
        return as(defaultLocker);
    }

    private final ConcurrentMap<O, ReleasableLock> lockMap = new ConcurrentHashMap<>();

    private MapperLocker() {
    }

    @Override
    public Lock lock(O object) {
        ReleasableLock lock = lockMap.get(object);
        while (true) {
            if (lock != null) {
                if (lock.apply(LockerKey.KEY)) {
                    lock.lock(LockerKey.KEY);
                    return lock;
                } else {
                    lockMap.remove(object, lock);
                }
            }
            lock = new RentReentrantLock();
            ReleasableLock old = lockMap.putIfAbsent(object, lock);
            if (old != null) {
                lock = old;
            }
        }
    }

    @Override
    public Lock lockInterruptibly(O object) throws InterruptedException {
        ReleasableLock lock = lockMap.get(object);
        while (true) {
            if (lock != null) {
                if (lock.apply(LockerKey.KEY)) {
                    try {
                        lock.lockInterruptibly(LockerKey.KEY);
                        return lock;
                    } catch (InterruptedException e) {
                        lock.release(LockerKey.KEY);
                        // 与 unlock 的回收形态一致：等待获取被中断的一方归还引用计数后，
                        // 同样由最后一个把计数归零的释放者负责销毁条目并从映射表移除
                        if (lock.destroy(LockerKey.KEY)) {
                            lockMap.remove(object, lock);
                        }
                        throw e;
                    }
                } else {
                    lockMap.remove(object, lock);
                }
            }
            lock = new RentReentrantLock();
            ReleasableLock old = lockMap.putIfAbsent(object, lock);
            if (old != null) {
                lock = old;
            }
        }
    }

    @Override
    public Optional<Lock> tryLock(O object) {
        ReleasableLock lock = lockMap.get(object);
        while (true) {
            if (lock != null) {
                if (lock.apply(LockerKey.KEY)) {
                    if (lock.tryLock(LockerKey.KEY)) {
                        return Optional.of(lock);
                    } else {
                        lock.release(LockerKey.KEY);
                        // 修复 diagnosis.md 案 #2（CI unit 工作流 GitHub Actions 运行号 run id 37165077819，2026-10-04）：
                        // 获取失败的一方在归还引用计数后，若本线程恰是最后一个把计数归零的释放者，
                        // 就由本线程销毁条目并从映射表移除（与 unlock 的回收形态一致）。修复前这条路径只做
                        // release 而不销毁，归零的条目永久残留在映射表里，size() 永远不会自行归零。
                        // destroy 是计数从零到销毁态的比较交换，只有最后一个归零的释放者能成功；与 apply
                        // 的比较交换协议交错时，后来者读到销毁态返回 false，走本方法既有的移除并重建分支。
                        if (lock.destroy(LockerKey.KEY)) {
                            lockMap.remove(object, lock);
                        }
                        return Optional.empty();
                    }
                } else {
                    lockMap.remove(object, lock);
                }
            }
            lock = new RentReentrantLock();
            ReleasableLock old = lockMap.putIfAbsent(object, lock);
            if (old != null) {
                lock = old;
            }
        }
    }

    @Override
    public Optional<Lock> tryLock(O object, long timeout, TimeUnit unit) throws InterruptedException {
        ReleasableLock lock = lockMap.get(object);
        while (true) {
            if (lock != null) {
                if (lock.apply(LockerKey.KEY)) {
                    try {
                        if (lock.tryLock(LockerKey.KEY, timeout, unit)) {
                            return Optional.of(lock);
                        } else {
                            lock.release(LockerKey.KEY);
                            // 与即时 tryLock 的获取失败分支同形：限时等待失败的一方归还计数后，
                            // 由最后一个把计数归零的释放者销毁条目并从映射表移除
                            if (lock.destroy(LockerKey.KEY)) {
                                lockMap.remove(object, lock);
                            }
                            return Optional.empty();
                        }
                    } catch (InterruptedException e) {
                        lock.release(LockerKey.KEY);
                        // 与 lockInterruptibly 的中断分支同形：等待被中断的一方归还计数后尝试销毁回收
                        if (lock.destroy(LockerKey.KEY)) {
                            lockMap.remove(object, lock);
                        }
                        throw e;
                    }
                } else {
                    lockMap.remove(object, lock);
                }
            }
            lock = new RentReentrantLock();
            ReleasableLock old = lockMap.putIfAbsent(object, lock);
            if (old != null) {
                lock = old;
            }
        }
    }

    @Override
    public void unlock(O object, Lock lock) {
        if (lock != null) {
            if (lock instanceof ReleasableLock) {
                ReleasableLock releasableLock = (ReleasableLock) lock;
                try {
                    releasableLock.unlock(LockerKey.KEY);
                } finally {
                    releasableLock.release(LockerKey.KEY);
                    if (releasableLock.destroy(LockerKey.KEY)) {
                        lockMap.remove(object, releasableLock);
                    }
                }
            } else {
                lock.unlock();
            }
        }
    }

    public int size() {
        return lockMap.size();
    }

    @Override
    public String toString() {
        return "MapObjectLocker{" + "lockMap=" + lockMap + '}';
    }

}
