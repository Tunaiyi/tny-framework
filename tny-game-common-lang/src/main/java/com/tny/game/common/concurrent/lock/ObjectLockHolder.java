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

import com.tny.game.common.concurrent.*;
import com.tny.game.common.utils.*;
import org.slf4j.*;

import java.util.Map.Entry;
import java.util.concurrent.*;

/**
 * 所有类型对象锁的持有器
 *
 * @author KGTny
 */
@SuppressWarnings({"rawtypes"})
class ObjectLockHolder {

    private static final Logger LOG = LoggerFactory.getLogger(LogAide.LOCK);

    private static final long GC_TIME = 600; // s

    private static final long SHOW_TIME = 30; // s

    /**
     * 持有者集合
     */
    private final ConcurrentHashMap<Class, Holder> holders = new ConcurrentHashMap<>();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10, new CoreThreadFactory("LockMonitorPool", true));

    ObjectLockHolder() {
        /*
         */
        Runnable monitorRunnable = () -> {
            for (Class clazz : ObjectLockHolder.this.holders.keySet()) {
                LOG.info("#链锁池信息#{} 类型的锁数量为: {} ", clazz, ObjectLockHolder.this.count(clazz));
            }
            ObjectLockHolder.this.scheduler.schedule((Runnable) this, SHOW_TIME, TimeUnit.SECONDS);
        };
        this.scheduler.schedule(monitorRunnable, SHOW_TIME, TimeUnit.SECONDS);
        /*
         */
        Runnable gcRunnable = new Runnable() {

            @Override
            public void run() {
                for (Entry<Class, Holder> entry : ObjectLockHolder.this.holders.entrySet()) {
                    entry.getValue().removeTimeOutLock();
                }
                ObjectLockHolder.this.scheduler.schedule(this, GC_TIME, TimeUnit.SECONDS);
            }
        };
        this.scheduler.schedule(gcRunnable, GC_TIME, TimeUnit.SECONDS);
    }

    /**
     * 单一class类型对象锁持有器
     *
     * @author KGTny
     */
    public static class Holder {

        /**
         * 对象实例与其对应的锁缓存
         */
        private final ConcurrentMap<Comparable<?>, ObjectReadWriteLock> locks = new ConcurrentHashMap<>();// 5000,
        // 0.5f

        /**
         * 创建一个持有者实例
         */
        public Holder() {
        }

        /**
         * 获取对象锁,并将锁设置为持有状态
         *
         * @param object
         * @return
         */
        public ObjectReadWriteLock getLock(LockEntity object, LockType lockType) {
            // 过期重建原子化（原"get→beGot失败→remove→put 覆写并可能返回已死旧锁"
            // 会让并发获取者各持新旧实例——同实体互斥击穿）
            Comparable<?> identity = object.getIdentity();
            while (true) {
                ObjectReadWriteLock existing = this.locks.get(identity);
                if (existing != null) {
                    if (existing.beGot(lockType)) {
                        return existing;
                    }
                    // 条件移除：只删自己判定过期的那把，不误删他人刚装好的新条目
                    this.locks.remove(identity, existing);
                }
                ObjectReadWriteLock created = this.locks.computeIfAbsent(identity,
                        key -> new ObjectReadWriteLock(object));
                if (created.beGot(lockType)) {
                    return created;
                }
                // 极端窗口（新建即被判定过期/持有）：摘除重试，最终收敛到唯一实例
                this.locks.remove(identity, created);
            }
        }

        /**
         * 创建对象锁
         *
         * @param object
         * @return
         */
        private ObjectReadWriteLock createLock(LockEntity object) {
            // 保留供旧调用形态：改为原子装载（put 覆写会顶掉并发者条目）
            return this.locks.computeIfAbsent(object.getIdentity(), key -> new ObjectReadWriteLock(object));
        }

        /**
         * 获取锁的数量
         *
         * @return
         */
        public int count() {
            return this.locks.size();
        }

        protected void removeTimeOutLock() {
            for (Entry<Comparable<?>, ObjectReadWriteLock> entry : this.locks.entrySet()) {
                ObjectReadWriteLock oldValue = entry.getValue();
                if (oldValue.isTimeOut()) {
                    Object oldKey = entry.getKey();
                    this.locks.remove(oldKey, oldValue);
                }
            }
        }

    }

    /**
     * 获取指定对象实例的对象锁
     *
     * @param object 要获取锁的对象实例
     * @return
     */
    public ObjectReadWriteLock getLock(LockEntity object, LockType lockType) {
        Holder holder = this.getHolder(object.getClass());
        ObjectReadWriteLock lock = holder.getLock(object, lockType);
        return lock;
    }

    /**
     * 获取某类实例的锁持有者
     *
     * @param clazz 指定类型
     * @return
     */
    private Holder getHolder(Class clazz) {
        Holder holder = this.holders.get(clazz);
        if (holder != null) {
            return holder;
        }
        this.holders.putIfAbsent(clazz, new Holder());
        return this.holders.get(clazz);
    }

    /**
     * 获取指定类型的锁的数量
     *
     * @param clazz
     * @return
     */
    private int count(Class<?> clazz) {
        if (this.holders.containsKey(clazz)) {
            Holder holder = this.getHolder(clazz);
            return holder.count();
        }
        return 0;
    }

}
