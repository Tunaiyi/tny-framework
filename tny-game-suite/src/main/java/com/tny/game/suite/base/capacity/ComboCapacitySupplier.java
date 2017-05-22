package com.tny.game.suite.base.capacity;

import com.tny.game.number.NumberUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 组合能力提供起
 * Created by Kun Yang on 16/4/12.
 */
public interface ComboCapacitySupplier extends CapacitySupplier {

    /**
     * @return 依赖能力提供器
     */
    Collection<? extends CapacitySupplier> dependSuppliers();

    @Override
    default boolean isHasValue(Capacity capacity) {
        return dependSuppliers().stream().anyMatch(s -> s.isHasValue(capacity));
    }

    @Override
    default Number getValue(Capacity capacity) {
        return getValue(capacity, null);
    }

    @Override
    default Number getValue(Capacity capacity, Number defaultNum) {
        Number total = null;
        for (CapacitySupplier supplier : dependSuppliers()) {
            if (total == null)
                total = supplier.getValue(capacity);
            else
                total = NumberUtils.add(supplier.getValue(capacity, 0), total);
        }
        return total == null ? defaultNum : total;
    }

    @Override
    default Map<Capacity, Number> getAllCapacityValue() {
        Map<Capacity, Number> numberMap = new HashMap<>();
        for (CapacitySupplier supplier : dependSuppliers()) {
            supplier.getAllCapacityValue().forEach((c, num) -> numberMap.merge(c, num, NumberUtils::add));
        }
        return numberMap;
    }

    // @Override
    // default Set<Capacity> getSupplyCapacities() {
    //     return Collections.emptySet();
    // }

}
