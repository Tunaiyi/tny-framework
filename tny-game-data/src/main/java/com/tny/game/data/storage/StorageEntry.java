package com.tny.game.data.storage;

import com.tny.game.data.exception.*;

/**
 * <p>
 */
public class StorageEntry<K extends Comparable<K>, O> {

    private K key;

    private O value;

    private StorageState state;

    private long clearAt;

    public StorageEntry(K key, O value, StorageOperation operation) {
        this.key = key;
        this.value = value;
        this.state = operation.getDefaultStatus();
    }

    public K getKey() {
        return key;
    }

    public O getValue() {
        return value;
    }

    public StorageState getState() {
        return state;
    }

    public boolean isDelete() {
        return state == StorageState.DELETE || state == StorageState.DELETED;
    }

    public void updateState(O object, StorageOperation operation) {
        if (!operation.isCanOperate(this.state))
            throw new StoreOperateException("[{}] submit exception, can not {} in {} state", this.value, operation, state);
        this.state = operation.getOperateState(this.state);
        this.value = object;
        if (this.state != StorageState.DELETED)
            clearAt = -1;
    }

    public void deleted(long delayRemove) {
        if (this.state == StorageState.DELETE) {
            this.state = StorageState.DELETED;
            this.clearAt = System.currentTimeMillis() + delayRemove;
        }
    }

    public boolean isNeedClear(long now) {
        return now >= this.clearAt;
    }
}
