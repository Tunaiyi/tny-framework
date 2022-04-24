package com.tny.game.basics.item.capacity;

import com.tny.game.common.number.*;

import java.util.*;

/**
 * 组合能力提供器
 * Created by Kun Yang on 16/4/12.
 */
public interface CompositeCapacitySupplier extends CapacitySupplier, CapableComposition {

    @Override
    default boolean isHasCapacity(Capacity capacity) {
        return isWorking() && suppliersStream().anyMatch(s -> s.isHasCapacity(capacity));
    }

    @Override
    default Map<Capacity, Number> getAllCapacities() {
        Map<Capacity, Number> numberMap = new HashMap<>();
        suppliers()
                .forEach(s -> s.getAllCapacities().forEach((c, num) -> numberMap.merge(c, num, NumberAide::add)));
        return numberMap;
    }

}
