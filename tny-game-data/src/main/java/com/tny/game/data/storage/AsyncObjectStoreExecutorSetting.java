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

package com.tny.game.data.storage;

/**
 * <p>
 */
public class AsyncObjectStoreExecutorSetting {

    private int parallelSize = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 每次持久化数量
     */
    private int step = 3000;

    /**
     * 失败尝试次数
     */
    private int tryTime = 3;

    /**
     * 每次暂停空闲等待间隔时间
     */
    private long idleInterval = 15000;

    /**
     * 持久化 step 个对象后仍然有未持久化对象的等待间隔时间
     */
    private long yieldInterval = 50;

    /**
     * 关闭等待时间
     */
    private long shutdownWaitTimeout = 30000;

    public int getStep() {
        return step;
    }

    public int getTryTime() {
        return tryTime;
    }

    public long getIdleInterval() {
        return idleInterval;
    }

    public long getShutdownWaitTimeout() {
        return shutdownWaitTimeout;
    }

    public int getParallelSize() {
        return parallelSize;
    }

    public long getYieldInterval() {
        return yieldInterval;
    }

    public AsyncObjectStoreExecutorSetting setYieldInterval(long yieldInterval) {
        this.yieldInterval = yieldInterval;
        return this;
    }

    public AsyncObjectStoreExecutorSetting setParallelSize(int parallelSize) {
        this.parallelSize = parallelSize;
        return this;
    }

    public AsyncObjectStoreExecutorSetting setStep(int step) {
        this.step = step;
        return this;
    }

    public AsyncObjectStoreExecutorSetting setTryTime(int tryTime) {
        this.tryTime = tryTime;
        return this;
    }

    public AsyncObjectStoreExecutorSetting setIdleInterval(long idleInterval) {
        this.idleInterval = idleInterval;
        return this;
    }

    public AsyncObjectStoreExecutorSetting setShutdownWaitTimeout(long shutdownWaitTimeout) {
        this.shutdownWaitTimeout = shutdownWaitTimeout;
        return this;
    }

}
