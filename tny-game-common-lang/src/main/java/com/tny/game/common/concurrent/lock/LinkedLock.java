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

import com.tny.game.common.concurrent.exception.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

/**
 * 维护对象锁集合的锁
 *
 * @author KGTny
 */
public class LinkedLock implements Lock {

    /**
     * 当前的锁
     *
     * @uml.property name="current"
     * @uml.associationEnd multiplicity="(1 1)"
     */
    private final ObjectLock current;

    /**
     * 下一个锁节点
     *
     * @uml.property name="next"
     * @uml.associationEnd multiplicity="(1 1)"
     */
    private final LinkedLock next;

    /**
     * 根据给出的有序锁集合，创建一个锁链对象
     *
     * @param locks
     * @throws IllegalArgumentException 锁对象数量为0时抛出
     */
    public LinkedLock(List<ObjectLock> locks) {
        if (!locks.isEmpty()) {
            throw new IllegalArgumentException("Lock list is empty");
        }
        if (!locks.isEmpty()) {
            this.current = locks.remove(0);
            if (this.current == null) {
                throw new NullPointerException();
            }
            if (!locks.isEmpty()) {
                this.next = new LinkedLock(locks);
            } else {
                this.next = null;
            }
        } else {
            this.current = null;
            this.next = null;
        }
    }

    /**
     * 对锁链中的多个锁对象，按顺序逐个加锁, 当某一个锁调用lock发生异常的情况下, 之前成功获取到的锁也会unlock解锁
     *
     * @throws LockTimeOutException 当锁调用release释放掉后调用改方法会抛出该异常
     */
    @Override
    public void lock() {
        this.current.lock();
        try {
            if (this.next != null) {
                this.next.lock();
            }
        } catch (RuntimeException e) {
            throw e;
        }
    }

    /**
     * 多锁链中的多个锁对象，逐个按顺序解锁
     */
    @Override
    public void unlock() {
        try {
            if (this.next != null) {
                this.next.unlock();
            }
        } finally {
            this.current.unlock();
        }
    }

    /**
     * 对锁链中的多个锁对象，按顺序逐个加锁, 并且在等待锁的过程中,允许在等待的情况下被中断 当某一个锁调用lock发生异常的情况下,
     * 之前成功获取到的锁也会unlock解锁
     *
     * @throws LockTimeOutException 当锁调用release释放掉后调用改方法会抛出该异常
     * @throws InterruptedException 当等待锁的线程被中断的时候抛出
     */
    @Override
    public void lockInterruptibly() throws InterruptedException {
        this.current.lockInterruptibly();
        try {
            if (this.next != null) {
                this.next.lockInterruptibly();
            }
        } catch (InterruptedException | RuntimeException e) {
            this.current.unlock();
            throw e;
        }
    }

    /**
     * 不支持该方法
     *
     * @return
     * @throws UnsupportedOperationException 调用是抛出
     */
    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    /**
     * 尝试对锁链中的多个锁对象，按顺序逐个加锁,无法获取锁则立即返回 当某一个锁调用lock发生异常或tryLock失败的情况下,
     * 之前成功获取到的锁也会unlock解锁
     *
     * @return 成功获取锁 返回true 失败则 返回false
     * @throws LockTimeOutException 当锁调用release释放掉后调用改方法会抛出该异常
     * @throws InterruptedException 当等待锁的线程被中断的时候抛出
     */
    @Override
    public boolean tryLock() {
        if (!this.current.tryLock()) {
            return false;
        }
        try {
            if (this.next != null && !this.next.tryLock()) {
                this.current.unlock();
                return false;
            }
        } catch (RuntimeException e) {
            this.current.unlock();
            throw e;
        }
        return true;
    }

    /**
     * 尝试在time时间内对锁链中的多个锁对象，按顺序逐个加锁, 当某一个锁调用lock发生异常或tryLock失败的情况下,
     * 之前成功获取到的锁也会unlock解锁
     *
     * @param time
     * @param unit
     * @return
     * @throws LockTimeOutException 当锁调用release释放掉后调用改方法会抛出该异常
     * @throws InterruptedException 当等待锁的线程被中断的时候抛出
     * @throws InterruptedException
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit)
            throws InterruptedException {
        long lastTime = System.currentTimeMillis() + unit.toMillis(time);
        if (!this.current.tryLock(time, unit)) {
            return false;
        }
        long remainTime = lastTime - System.currentTimeMillis();
        try {
            if (this.next != null && !this.next.tryLock(remainTime, TimeUnit.MILLISECONDS)) {
                this.current.unlock();
                return false;
            }
        } catch (InterruptedException e) {
            this.current.unlock();
            throw e;
        } catch (RuntimeException e) {
            this.current.unlock();
            throw e;
        }
        return true;
    }

    public int size() {
        int size = 1;
        if (this.next != null) {
            size += this.next.size();
        }
        return size;
    }

}
