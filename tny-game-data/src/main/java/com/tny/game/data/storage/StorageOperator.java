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

import com.google.common.collect.ImmutableList;
import com.tny.game.data.accessor.*;

import java.util.*;
import java.util.function.BiFunction;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * 异步持久化实体状态枚举
 *
 * @author KGTny
 */
public enum StorageOperator {

    /**
     * 正常状态
     */
    NORMAL(0, null, null),

    /**
     * 插入状态 属于提交持久化状态
     */
    INSERT(1, StorageAccessor::insert, StorageAccessor::insert),
    /**
     * 更新状态 属于提交持久化状态
     */
    UPDATE(2, StorageAccessor::update, StorageAccessor::update),

    /**
     * 保存状态 属于提交持久化状态
     */
    SAVE(3, StorageAccessor::save, StorageAccessor::save),

    /**
     * 删除状态 属于提交持久化状态
     */
    DELETE(4,
            (accessor, value) -> {
                accessor.delete(value);
                return true;
            },
            (accessor, values) -> {
                accessor.delete(values);
                return ImmutableList.of();
            }),
    /**
     * 已删除
     */
    DELETED(5, null, null),

    //
    ;

    private final int index;

    private final BiFunction<StorageAccessor<?, ?>, Object, Boolean> singleOperate;

    private final BiFunction<StorageAccessor<?, ?>, Collection<?>, Collection<?>> multiOperate;

    <T> StorageOperator(int index,
            BiFunction<StorageAccessor<?, T>, T, Boolean> singleOperate,
            BiFunction<StorageAccessor<?, T>, Collection<T>, Collection<T>> multiOperate) {
        this.index = index;
        this.singleOperate = as(singleOperate);
        this.multiOperate = as(multiOperate);
    }

    public int getIndex() {
        return index;
    }

    public boolean isNeedOperate() {
        return this.multiOperate != null && this.singleOperate != null;
    }

    public <O> boolean operate(StorageAccessor<?, O> accessor, O object) {
        if (this.singleOperate == null) {
            return true;
        }
        return this.singleOperate.apply(accessor, object);
    }

    public <O> Collection<O> operate(StorageAccessor<?, O> accessor, Collection<O> objects) {
        if (this.multiOperate == null) {
            return Collections.emptyList();
        }
        return as(this.multiOperate.apply(accessor, objects));
    }

}
