package com.tny.game.suite.base.capacity;

import java.util.Map;
import java.util.Set;

/**
 * 游戏能力值提供器
 * Created by Kun Yang on 16/2/15.
 */
public interface ProxyTimeoutCapacitySupplier extends TimeoutCapacitySupplier, ProxyCapacitySupplier {

    @Override
    default Set<Capacity> getSupplyCapacities() {
        return TimeoutCapacitySupplier.super.getSupplyCapacities();
    }

    @Override
    default Map<Capacity, Number> getAllCapacityValue() {
        return TimeoutCapacitySupplier.super.getAllCapacityValue();
    }

    @Override
    default Number getValue(Capacity capacity, Number defaultValue) {
        return TimeoutCapacitySupplier.super.getValue(capacity, defaultValue);
    }

    @Override
    default Number getValue(Capacity capacity) {
        return TimeoutCapacitySupplier.super.getValue(capacity);
    }
    @Override
    default boolean isHasValue(Capacity capacity) {
        return TimeoutCapacitySupplier.super.isHasValue(capacity);
    }

}