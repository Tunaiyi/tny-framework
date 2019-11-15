package com.tny.game.suite.base.capacity;

import com.tny.game.common.number.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * 组合能力提供起
 * Created by Kun Yang on 16/4/12.
 */
public interface ComboCapacitySupplier extends CapacitySupplier {

    /**
     * @return 依赖能力提供器
     */
    Collection<? extends CapacitySupplier> dependSuppliers();

    default Stream<? extends CapacitySupplier> dependSuppliersStream() {
        return dependSuppliers().stream();
    }

    @Override
    default boolean isHasValue(Capacity capacity) {
        return dependSuppliersStream()
                .filter(CapacitySupplier::isSupplying)
                .anyMatch(s -> s.isHasValue(capacity));
    }

    @Override
    default Number getValue(Capacity capacity) {
        return getValue(capacity, null);
    }

    @Override
    default Number getValue(Capacity capacity, Number defaultNum) {
        return dependSuppliersStream()
                .map(s -> s.getValue(capacity))
                .filter(Objects::nonNull)
                .reduce(NumberAide::add)
                .orElse(defaultNum);
    }

    @Override
    default Map<Capacity, Number> getAllValues() {
        Map<Capacity, Number> numberMap = new HashMap<>();
        dependSuppliersStream()
                .forEach(s -> s.getAllValues().forEach((c, num) -> numberMap.merge(c, num, NumberAide::add)));
        return numberMap;
    }

    // @Override
    // default Set<Capacity> getSupplyCapacities() {
    //     return Collections.emptySet();
    // }

}
