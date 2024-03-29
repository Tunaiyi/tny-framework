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

import com.tny.game.data.exception.*;

/**
 * <p>
 */
public class StorageEntry<K extends Comparable<?>, O> {

    private final K key;

    private O value;

    private StorageOperator operator;

    private long clearAt;

    private int failureTimes;

    public StorageEntry(K key, O value, StorageAction action) {
        this.key = key;
        this.value = value;
        this.operator = action.getDefaultOperator();
    }

    public K getKey() {
        return key;
    }

    public O getValue() {
        return value;
    }

    public StorageOperator getOperator() {
        return operator;
    }

    public boolean isDelete() {
        return operator == StorageOperator.DELETE || operator == StorageOperator.DELETED;
    }

    /**
     * 更新操作
     *
     * @param object 值
     * @param action 操作
     * @return 如果改变返回 true, 否则返回 false
     */
    public boolean updateOperator(O object, StorageAction action) {
        if (!action.isCanOperate(this.operator)) {
            throw new StoreOperateException("[{}] submit exception, can not {} in {} state", this.value, action, operator);
        }
        StorageOperator old = this.operator;
        StorageOperator change = action.changeFrom(this.operator);
        if (old == change) {
            return true;
        }
        this.operator = change;
        this.value = object;
        this.failureTimes = 0;
        if (this.operator != StorageOperator.DELETED) {
            clearAt = -1;
        }
        return true;
    }

    public void operationFailed() {
        failureTimes++;
    }

    public int getFailureTimes() {
        return failureTimes;
    }

    public void deleted(long delayRemove) {
        if (this.operator == StorageOperator.DELETE) {
            this.operator = StorageOperator.DELETED;
            this.clearAt = System.currentTimeMillis() + delayRemove;
        }
    }

    public boolean isNeedClear(long now) {
        return this.operator == StorageOperator.DELETED && now >= this.clearAt;
    }

}
