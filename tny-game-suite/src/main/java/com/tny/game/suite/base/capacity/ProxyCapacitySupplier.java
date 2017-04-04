package com.tny.game.suite.base.capacity;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.tny.game.base.item.Item;

import java.util.Map;
import java.util.Set;

/**
 * 能力值提供七代理接口
 * Created by Kun Yang on 16/3/12.
 */
public interface ProxyCapacitySupplier extends CapacitySupplier {

    Item<?> item();

    CapacitySupply supply();

    @Override
    default long getID() {
        return item().getID();
    }

    @Override
    default int getItemID() {
        return item().getItemID();
    }

    @Override
    default long getPlayerID() {
        return item().getPlayerID();
    }

    @Override
    default boolean isHasValue(Capacity capacity) {
        return isSupplying() && supply().isHasValue(capacity);
    }

    @Override
    default Number getValue(Capacity capacity) {
        if (!isSupplying())
            return null;
        return supply().getValue(capacity);
    }

    @Override
    default Number getValue(Capacity capacity, Number defaultNum) {
        if (!isSupplying())
            return defaultNum;
        return supply().getValue(capacity, defaultNum);
    }

    @Override
    default Map<Capacity, Number> getAllCapacityValue() {
        if (!isSupplying())
            return ImmutableMap.of();
        return supply().getAllCapacityValue();
    }

    @Override
    default Set<Capacity> getSupplyCapacities() {
        if (!isSupplying())
            return ImmutableSet.of();
        return supply().getSupplyCapacities();
    }

}
