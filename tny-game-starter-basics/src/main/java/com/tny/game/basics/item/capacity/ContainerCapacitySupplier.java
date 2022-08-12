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

import com.google.common.collect.ImmutableMap;

import java.util.*;

/**
 * 能力值提供七代理接口
 * Created by Kun Yang on 16/3/12.
 */
public interface ContainerCapacitySupplier extends CapacitySupplier {

    CapacitySupply supply();

    @Override
    default boolean isHasCapacity(Capacity capacity) {
        return isWorking() && supply().isHasCapacity(capacity);
    }

    @Override
    default Number getCapacity(Capacity capacity) {
        if (!isWorking()) {
            return null;
        }
        return supply().getCapacity(capacity);
    }

    @Override
    default Number getCapacity(Capacity capacity, Number defaultNum) {
        if (!isWorking()) {
            return defaultNum;
        }
        return supply().getCapacity(capacity, defaultNum);
    }

    @Override
    default Map<Capacity, Number> getAllCapacities() {
        if (!isWorking()) {
            return ImmutableMap.of();
        }
        return supply().getAllCapacities();
    }

    @Override
    default void collectCapacities(CapacityCollector collector, Collection<? extends Capacity> capacities) {
        supply().collectCapacities(collector, capacities);
    }

    @Override
    default Set<CapacityGroup> getAllCapacityGroups() {
        return supply().getAllCapacityGroups();
    }

}
