package com.tny.game.data.cache;

import com.tny.game.common.utils.*;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.*;

/**
 * <p>
 */
public class CacheEntry<K extends Comparable<K>, O> {

    /**
     * id 获取器
     */
    private K id;

    /**
     * 持久化对象
     */
    private volatile WeakReference<O> object;

    /**
     * 持有对象，确保一定条件内不被回收
     */
    private volatile O holdObject;

    /**
     * 锁
     */
    private Lock lock = new ReentrantLock();

    /**
     * 释放策略
     */
    private ReleaseStrategy<K, O> releaseStrategy;

    /**
     * 是否可被其他对象替换
     */
    private boolean replaceable;


    CacheEntry(K id, O object, boolean replaceable, ReleaseStrategy<K, O> releaseStrategy) {
        this.setValue(Throws.checkNotNull(id, "{} id is null", object),
                Throws.checkNotNull(object, "{} value is null", id));
        this.releaseStrategy = releaseStrategy;
        this.replaceable = replaceable;
        this.visit();
    }

    private void setValue(K id, O value) {
        this.id = id;
        this.object = new WeakReference<>(value);
    }

    public K getKey() {
        return id;
    }

    public boolean isReplaceable() {
        return replaceable;
    }

    public O getObject() {
        return this.object.get();
    }


    /**
     * 尝试访问对象
     *
     * @return 访问成功返回true 失败为false
     */
    boolean tryVisit() {
        return this.visit() != null;
    }

    /**
     * 替换对象
     *
     * @param object 替换的对象
     * @return 返回是否替换成功
     */
    boolean replace(K id, O object) {
        Throws.checkArgument(this.id.equals(id), "current id {} not equals id {}", this.id, id);
        lock.lock();
        try {
            if (!this.tryVisit())
                return false;
            O returnObject = this.object.get();
            if (returnObject != object) {
                this.setValue(id, object);
                this.visit();
            }
            return true;
        } finally {
            lock.unlock();
        }

    }

    /**
     * 访问对象
     *
     * @return 返回对象
     */
    O visit() {
        lock.lock();
        try {
            O returnObject = this.object.get();
            if (returnObject != null) {
                this.releaseStrategy.visit();
                this.holdObject = returnObject;
            }
            return returnObject;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 释放对象
     *
     * @return 释放成功返回true，失败返回false
     */
    boolean release(long releaseAt) {
        lock.lock();
        try {
            Object object = this.holdObject;
            if ((object != null && this.releaseStrategy.release(this, releaseAt))) {
                this.holdObject = null;
            }
            return this.object.get() == null;
        } finally {
            lock.unlock();
        }
    }


    /**
     * 释放对象
     *
     * @return 释放成功返回true，失败返回false
     */
    void forceRelease() {
        lock.lock();
        try {
            this.object.clear();
            this.holdObject = null;
        } finally {
            lock.unlock();
        }
    }

}
