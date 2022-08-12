/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.basics.item.capacity;

import java.util.*;

/**
 * 与能力相关的
 * Created by Kun Yang on 16/3/23.
 */
public interface Capable {

    /**
     * @param capacity 能力类型
     * @return 获取  capacity 能力值
     */
    default Number getCapacity(Capacity capacity) {
        return getCapacity(capacity, capacity.getDefault());
    }

    /**
     * @param capacity   能力类型
     * @param defaultNum 默认值
     * @return 获取  capacity 能力值
     */
    Number getCapacity(Capacity capacity, Number defaultNum);

    /**
     * @param capacity 能力类型
     * @return 获取  capacity short 能力值
     */
    default short getShortCapacity(Capacity capacity) {
        return getCapacity(capacity, capacity.getDefault()).shortValue();
    }

    /**
     * @param capacity 能力类型
     * @return 获取  capacity int 能力值
     */
    default int getIntCapacity(Capacity capacity) {
        return getCapacity(capacity, capacity.getDefault()).intValue();
    }

    /**
     * @param capacity 能力类型
     * @return 获取  capacity long 能力值
     */
    default long getLongCapacity(Capacity capacity) {
        return getCapacity(capacity, capacity.getDefault()).longValue();
    }

    /**
     * @param capacity 能力类型
     * @return 获取  capacity boolean 能力值
     */
    default boolean getBooleanCapacity(Capacity capacity) {
        return getCapacity(capacity, capacity.getDefault()).intValue() > 0;
    }

    /**
     * @param capacity 能力类型
     * @return 获取  capacity double 能力值
     */
    default double getDoubleCapacity(Capacity capacity) {
        return getCapacity(capacity, capacity.getDefault()).doubleValue();
    }

    /**
     * @param capacity 能力类型
     * @return 获取  capacity float 能力值
     */
    default float getFloatCapacity(Capacity capacity) {
        return getCapacity(capacity, capacity.getDefault()).shortValue();
    }

    /**
     * 收集Supplier中指定capacities的能力值
     *
     * @param collector  收集器
     * @param capacities 能力值类型
     */
    default void collectCapacities(CapacityCollector collector, Capacity... capacities) {
        collectCapacities(collector, Arrays.asList(capacities));
    }

    /**
     * 收集Supplier中指定capacities的能力值
     *
     * @param collector  收集器
     * @param capacities 能力值类型
     */
    default void collectCapacities(CapacityCollector collector, Collection<? extends Capacity> capacities) {
        for (Capacity capacity : capacities)
            collector.collect(capacity, getCapacity(capacity));
    }

    /**
     * @return 获取所有能力值组
     */
    Set<CapacityGroup> getAllCapacityGroups();

}
