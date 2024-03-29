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

package com.tny.game.basics.item.capacity;

import java.util.Optional;

/**
 * Created by Kun Yang on 2017/7/17.
 */
public interface CapacityObjectQuerier {

    /**
     * @return 玩家ID
     */
    long getPlayerId();

    /**
     * 查找指定id的提供器
     *
     * @param id 提供器ID
     * @return 返回提供器
     */
    Optional<CapacitySupplier> findSupplier(long id);

    /**
     * 查找指定id的Combo提供器
     *
     * @param id 提供器ID
     * @return 返回Combo提供器
     */
    Optional<CompositeCapacitySupplier> findCompositeSupplier(long id);

    /**
     * 查找指定id的目标
     *
     * @param id 目标ID
     * @return 返回目标
     */
    Optional<Capabler> findCapabler(long id);

    /**
     * 指定id的提供器是否存在
     *
     * @param id 提供起ID
     * @return 存在返回true 不存在返回false
     */
    boolean isHasSupplier(long id);

    /**
     * 指定id的目标是否存在
     *
     * @param id 目标ID
     * @return 存在返回true 不存在返回false
     */
    boolean isHasCapabler(long id);

    // /**
    //  * 指定id的提供器是否存在capacity能力值
    //  *
    //  * @param id       id
    //  * @param capacity 能力值
    //  * @return 存在返回true 不存在返回false
    //  */
    // boolean isHasCapacity(long id, Capacity capacity);
    //
    // /**
    //  * 获取指定能力值提供器的capacity能力值
    //  *
    //  * @param id 提供器ID
    //  * @return 返回指定能力值, 如果不存在返回null;
    //  */
    // Map<Capacity, Number> getAllValues(long id);
    //
    // /**
    //  * 获取指定能力值提供器的capacity能力值
    //  *
    //  * @param id       提供器ID
    //  * @param capacity 指定提供器
    //  * @param defValue 默认值
    //  * @return 返回指定能力值, 如果不存在返回defValue;
    //  */
    // Number getValue(long id, Capacity capacity, Number defValue);
    //
    // /**
    //  * 获取指定能力值提供器的capacity能力值
    //  *
    //  * @param id       提供器ID
    //  * @param capacity 指定提供器
    //  * @return 返回指定能力值, 如果不存在返回null;
    //  */
    // default Number getValue(long id, Capacity capacity) {
    //     return getValue(id, capacity, null);
    // }
    //
    // /**
    //  * 获取指定能力值提供器的capacities能力值
    //  *
    //  * @param collector  能力值收集器
    //  * @param capacities 能力值类型
    //  * @return 返回类型, 存在的Capacity为null
    //  */
    // default void collectValues(long id, CapacityCollector collector, Capacity... capacities) {
    //     collectValues(id, collector, Arrays.asList(capacities));
    // }
    //
    // /**
    //  * * 获取指定能力值提供器的capacities能力值
    //  *
    //  * @param collector  能力值收集器
    //  * @param capacities 能力值类型
    //  * @return 返回类型, 存在的Capacity为null
    //  */
    // void collectValues(long id, CapacityCollector collector, Collection<? extends Capacity> capacities);

}
