package com.tny.game.suite.base.capacity;

import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public interface ProxyVisitorCapacityGoal extends ProxyGatherCapacityGoal {

    CapacityVisitor visitor();

    @Override
    default Collection<? extends CapacitySupplier> suppliers() {
        return visitor().findGoal(this.getID())
                .map(CapacityGather::suppliers)
                .orElse(ImmutableList.of());
    }

    @Override
    default Stream<? extends CapacitySupplier> suppliersStream() {
        return visitor().findGoal(this.getID())
                .map(CapacityGather::suppliersStream)
                .orElse(Stream.empty());
    }

}
