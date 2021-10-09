package com.tny.game.basics.item.capacity;

import com.google.common.collect.*;

import java.util.*;

/**
 * 能力值提供七代理接口
 * Created by Kun Yang on 16/3/12.
 */
public interface ProxyVisitorCapacitySupplier extends CapacitySupplier {

    CapacityVisitor visitor();

    @Override
    default boolean isHasValue(Capacity capacity) {
        return isSupplying() && visitor().findSupplier(this.getId())
                                         .map(s -> s.isHasValue(capacity))
                                         .orElse(false);
    }

    @Override
    default Number getValue(Capacity capacity) {
        return getValue(capacity, null);
    }

    @Override
    default Number getValue(Capacity capacity, Number defaultNum) {
        if (!isSupplying())
            return null;
        return visitor().findSupplier(this.getId())
                        .map(s -> s.getValue(capacity))
                        .orElse(defaultNum);
    }

    @Override
    default Map<Capacity, Number> getAllValues() {
        if (!isSupplying())
            return ImmutableMap.of();
        return visitor().findSupplier(this.getId())
                        .map(CapacitySupplier::getAllValues)
                        .orElse(ImmutableMap.of());
    }

    @Override
    default void collectValues(CapacityCollector collector, Collection<? extends Capacity> capacities) {
        visitor().findSupplier(this.getId())
                 .ifPresent(s -> s.collectValues(collector, capacities));
    }

    @Override
    default Set<CapacityGroup> getAllCapacityGroups() {
        return visitor().findSupplier(this.getId())
                        .map(CapacitySupply::getAllCapacityGroups)
                        .orElse(ImmutableSet.of());
    }
}
