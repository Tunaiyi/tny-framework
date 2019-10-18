package com.tny.game.data.storage;

/**
 * <p>
 */
public class QueueObjectStorageSetting {

    /**
     * 每次持久化数量
     */
    private int step = 3000;

    /**
     * 失败尝试次数
     */
    private int tryTime = 3;

    /**
     * 每次暂停时间
     */
    private long waitTime = 2000;

    /**
     * 删除延迟移除
     */
    private long deletedRemoveDelay = 5000;

    public int getStep() {
        return step;
    }


    public int getTryTime() {
        return tryTime;
    }

    public long getWaitTime() {
        return waitTime;
    }

    public long getDeletedRemoveDelay() {
        return deletedRemoveDelay;
    }

    public QueueObjectStorageSetting setStep(int step) {
        this.step = step;
        return this;
    }

    public QueueObjectStorageSetting setTryTime(int tryTime) {
        this.tryTime = tryTime;
        return this;
    }

    public QueueObjectStorageSetting setWaitTime(long waitTime) {
        this.waitTime = waitTime;
        return this;
    }

    public QueueObjectStorageSetting setDeletedRemoveDelay(long deletedRemoveDelay) {
        this.deletedRemoveDelay = deletedRemoveDelay;
        return this;
    }
}
