package com.tny.game.suite.base.capacity;

import com.google.common.collect.ImmutableMap;

import java.util.*;

/**
 * 能力值提供七代理接口
 * Created by Kun Yang on 16/3/12.
 */
public interface ProxySupplyCapacitySupplier extends CapacitySupplier {

    CapacitySupply supply();

    @Override
    default boolean isHasValue(Capacity capacity) {
        return isSupplying() && supply().isHasValue(capacity);
    }

    @Override
    default Number getValue(Capacity capacity) {
        if (!isSupplying()) {
            return null;
        }
        return supply().getValue(capacity);
    }

    @Override
    default Number getValue(Capacity capacity, Number defaultNum) {
        if (!isSupplying()) {
            return defaultNum;
        }
        return supply().getValue(capacity, defaultNum);
    }

    @Override
    default Map<Capacity, Number> getAllValues() {
        if (!isSupplying()) {
            return ImmutableMap.of();
        }
        return supply().getAllValues();
    }

    @Override
    default void collectValues(CapacityCollector collector, Collection<? extends Capacity> capacities) {
        supply().collectValues(collector, capacities);
    }

    @Override
    default Set<CapacityGroup> getAllCapacityGroups() {
        return supply().getAllCapacityGroups();
    }

}
