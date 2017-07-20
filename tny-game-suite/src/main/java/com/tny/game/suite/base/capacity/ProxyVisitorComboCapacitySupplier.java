package com.tny.game.suite.base.capacity;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
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
        return visitor().findComboSupplier(this.getID())
                .map(ComboCapacitySupplier::dependSuppliers)
                .orElse(ImmutableList.of());
    }

    @Override
    default Stream<? extends CapacitySupplier> dependSuppliersStream() {
        return visitor().findComboSupplier(this.getID())
                .map(ComboCapacitySupplier::dependSuppliersStream)
                .orElse(Stream.empty());
    }

}
