/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.capacity.event;

import com.tny.game.basics.item.capacity.*;

import java.util.Collection;

/**
 * 能力值存储监听器
 * Created by Kun Yang on 2017/7/19.
 */
public interface CapacityStorerListener {

    /**
     * 触发链接Supplier
     *
     * @param storer    存储器
     * @param suppliers 链接的能力值列表
     */
    default void onLinkSupplier(CapacityObjectStorer storer, Collection<ExpireCapacitySupplier> suppliers) {
    }

    /**
     * 触发存储Supplier
     *
     * @param storer    存储器
     * @param suppliers 存储的能力值列表
     */
    default void onSaveSupplier(CapacityObjectStorer storer, Collection<ExpireCapacitySupplier> suppliers) {
    }

    ;

    /**
     * 触发删除Supplier
     *
     * @param storer    存储器
     * @param suppliers 删除的能力值列表
     */
    default void onDeleteSupplier(CapacityObjectStorer storer, Collection<ExpireCapacitySupplier> suppliers) {
    }

    ;

    /**
     * 触发有效时间变化
     *
     * @param storer    存储器
     * @param suppliers 改变的能力值列表
     */
    default void onExpireSupplier(CapacityObjectStorer storer, Collection<ExpireCapacitySupplier> suppliers) {
    }

    ;

    /**
     * 触发存储Capabler
     *
     * @param storer    存储器
     * @param capablers 存储的目标列表
     */
    default void onSaveCapabler(CapacityObjectStorer storer, Collection<ExpireCapabler> capablers) {
    }

    ;

    /**
     * 触发删除Capabler
     *
     * @param storer    存储器
     * @param capablers 删除的目标列表
     */
    default void onDeleteCapabler(CapacityObjectStorer storer, Collection<ExpireCapabler> capablers) {
    }

    ;

    /**
     * 触发有效时间变化
     *
     * @param storer    存储器
     * @param capablers 改变的目标列表
     */
    default void onExpireCapabler(CapacityObjectStorer storer, Collection<ExpireCapabler> capablers) {
    }

    ;

}
