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
            Comparable<?> identity = object.getIdentity();
            ObjectReadWriteLock result = this.locks.get(identity);
            if (result == null || !result.beGot(lockType)) {
                this.locks.remove(identity, result);
                result = this.createLock(object);
                result.beGot(lockType);
            }
            return result;
        }

        /**
         * 创建对象锁
         *
         * @param object
         * @return
         */
        private ObjectReadWriteLock createLock(LockEntity object) {
            ObjectReadWriteLock result = new ObjectReadWriteLock(object);
            ObjectReadWriteLock oldLock = this.locks.put(object.getIdentity(), result);
            if (oldLock == null) {
                oldLock = result;
            }
            return oldLock;
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
