package com.tny.game.suite.base.capacity;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 组合能力提供起
 * Created by Kun Yang on 16/4/12.
 */
public interface ComboCapacitySupplier extends CapacitySupplier {

    /**
     * @return 依赖能力提供器
     */
    Collection<CapacitySupplier> dependSuppliers();

    @Override
    default boolean isHasValue(Capacity capacity) {
        return false;
    }

    @Override
    default Map<Capacity, Number> getAllCapacityValue() {
        return Collections.emptyMap();
    }

    @Override
    default Set<Capacity> getSupplyCapacities() {
        return Collections.emptySet();
    }

}
