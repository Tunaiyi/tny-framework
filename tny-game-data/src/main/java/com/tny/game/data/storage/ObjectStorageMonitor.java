package com.tny.game.data.storage;

import java.util.concurrent.atomic.LongAdder;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/15 6:40 下午
 */
public class ObjectStorageMonitor {

    private final Class<?> objectClass;

    private final LongAdder insertCounter = new LongAdder();

    private final LongAdder updateCounter = new LongAdder();

    private final LongAdder saveCounter = new LongAdder();

    private final LongAdder deleteCounter = new LongAdder();

    private final LongAdder failedCounter = new LongAdder();

    public ObjectStorageMonitor(Class<?> objectClass) {
        this.objectClass = objectClass;
    }

    public Class<?> getObjectClass() {
        return objectClass;
    }

    public void onFailure() {
        this.failedCounter.increment();
    }

    public void onSuccess(StorageOperator operator) {
        switch (operator) {
            case INSERT:
                this.insertCounter.increment();
                break;
            case UPDATE:
                this.updateCounter.increment();
                break;
            case SAVE:
                this.saveCounter.increment();
                break;
            case DELETE:
                this.deleteCounter.increment();
                break;
        }
    }

    public long getInsertNumber() {
        return this.insertCounter.longValue();
    }

    public long getUpdateNumber() {
        return this.updateCounter.longValue();
    }

    public long getSaveNumber() {
        return this.saveCounter.longValue();
    }

    public long getDeleteNumber() {
        return this.deleteCounter.longValue();
    }

    public long getFailedNumber() {
        return this.failedCounter.longValue();
    }

}
