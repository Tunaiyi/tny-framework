package com.tny.game.suite.base.capacity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public interface ProxyVisitorCapacityGoal extends ProxyGatherCapacityGoal {

    CapacityVisitor visitor();

    @Override
    default Collection<? extends CapacitySupplier> suppliers() {
        return visitor().findGoal(this.getId())
                .map(CapacityGather::suppliers)
                .orElse(ImmutableList.of());
    }

    @Override
    default Stream<? extends CapacitySupplier> suppliersStream() {
        return visitor().findGoal(this.getId())
                .map(CapacityGather::suppliersStream)
                .orElse(Stream.empty());
    }

    @Override
    default Set<CapacityGroup> getSuppliersCapacityGroups() {
        return visitor().findGoal(this.getId())
                .map(CapacityGoal::getSuppliersCapacityGroups)
                .orElse(ImmutableSet.of());
    }
}
