/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.concurrent.lock;

import com.tny.game.common.concurrent.exception.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

/**
 * 读写对象锁
 *
 * @author KGTny
 */
@SuppressWarnings({"rawtypes"})
class ObjectReadWriteLock extends AbstractTimeLimiter implements ObjectLock {

    public static final long serialVersionUID = 4140777286971432255L;

    /**
     * 對象類型
     *
     * @uml.property name="objectClass"
     */
    private final Class objectClass;

    /**
     * 强引用表示
     *
     * @uml.property name="identity"
     */
    private final Comparable identity;

    /**
     * 当前线程持有的锁类型
     *
     * @uml.property name="lockType"
     */
    private final ThreadLocal<LockType> lockType;

    /**
     * 读写锁
     *
     * @uml.property name="lock"
     */
    private final ReentrantReadWriteLock lock;

    /**
     * 获取对象的表示
     *
     * @param object
     * @return
     */
    public static Object getIdentity(Object object) {
        return LockEntity.class.isAssignableFrom(object.getClass()) ?
               ((LockEntity) object).getIdentity() :
               System.identityHashCode(object);
    }

    /**
     * 创建对象读写锁,默认是非公平锁
     *
     * @param object 相关联的对象
     */
    public ObjectReadWriteLock(LockEntity object) {
        //600000
        this(object, false, 600000);
    }

    /**
     * 创建对象读写锁
     *
     * @param object 相关联的对象
     * @param fair   是否是公平锁
     */
    public ObjectReadWriteLock(LockEntity object, boolean fair, long interval) {
        super(interval);
        this.lock = new ReentrantReadWriteLock();
        this.lockType = new ThreadLocal<>();
        this.objectClass = object.getClass();
        this.identity = object.getIdentity();
        this.last.set(System.currentTimeMillis() + this.interval);
    }

    public boolean beGot(LockType type) {
        if (update()) {
            this.lockType.set(type == LockType.READ ? LockType.READ : LockType.WRITE);
            return true;
        }
        return false;
    }


    @Override
    public void lock() {
        if (!this.update()) {
            throw new LockTimeOutException("[" + Thread.currentThread().getName() +
                                           "] Thread does not hold the lock " + this.identity);
        }
        this.getCurrentLock().lock();
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        if (!this.update()) {
            throw new LockTimeOutException("[" + Thread.currentThread().getName() +
                                           "] Thread does not hold the lock " + this.identity);
        }
        this.getCurrentLock().lockInterruptibly();
    }

    @Override
    public boolean tryLock() {
        if (!this.update()) {
            throw new LockTimeOutException("[" + Thread.currentThread().getName() +
                                           "] Thread does not hold the lock " + this.identity);
        }
        return this.getCurrentLock().tryLock();
    }

    @Override
    public void unlock() {
        this.getCurrentLock().unlock();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        if (!this.update()) {
            throw new LockTimeOutException("[" + Thread.currentThread().getName() +
                                           "] Thread does not hold the lock " + this.identity);
        }
        return this.getCurrentLock().tryLock(time, unit);
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    private Lock getCurrentLock() {
        return this.lockType.get() == LockType.READ ? this.lock.readLock() : this.lock.writeLock();
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compareTo(ObjectReadWriteLock otherLock) {
        if (this.objectClass != otherLock.objectClass) {
            if (this.objectClass.hashCode() < otherLock.objectClass.hashCode()) {
                return -1;
            } else if (this.objectClass.hashCode() > otherLock.objectClass.hashCode()) {
                return 1;
            }
            return this.objectClass.getName().compareTo(otherLock.objectClass.getName());
        }
        return this.identity.compareTo(otherLock.identity);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                 + ((this.identity == null) ? 0 : this.identity.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ObjectReadWriteLock other = (ObjectReadWriteLock) obj;
        if (this.identity == null) {
            if (other.identity != null) {
                return false;
            }
        } else if (!this.identity.equals(other.identity)) {
            return false;
        }
        return true;
    }

}
