package com.tny.game.suite.base.capacity;

import com.google.common.collect.*;

import java.util.*;
import java.util.stream.Stream;

/**
 * 组合能力提供起
 * Created by Kun Yang on 16/4/12.
 */
public interface ProxyVisitorComboCapacitySupplier extends ComboCapacitySupplier {

    /**
     * @return 能力值访问器
     */
    CapacityVisitor visitor();

    @Override
    default Collection<? extends CapacitySupplier> dependSuppliers() {
        return visitor().findComboSupplier(this.getId())
                        .map(ComboCapacitySupplier::dependSuppliers)
                        .orElse(ImmutableList.of());
    }

    @Override
    default Stream<? extends CapacitySupplier> dependSuppliersStream() {
        return visitor().findComboSupplier(this.getId())
                        .map(ComboCapacitySupplier::dependSuppliersStream)
                        .orElse(Stream.empty());
    }

    @Override
    default Set<CapacityGroup> getAllCapacityGroups() {
        return visitor().findSupplier(this.getId())
                        .map(CapacitySupply::getAllCapacityGroups)
                        .orElse(ImmutableSet.of());
    }
}
