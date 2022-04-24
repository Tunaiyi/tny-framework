package com.tny.game.suite.base.capacity;

import java.util.*;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public interface ProxyGatherCapacityGoal extends CapacityGoal {

    CapacityGather gather();

    @Override
    default Collection<? extends CapacitySupplier> suppliers() {
        return gather().suppliers();
    }

    @Override
    default Set<CapacityGroup> getSuppliersCapacityGroups() {
        return gather().getSuppliersCapacityGroups();
    }

}
