package com.tny.game.suite.base.capacity;

import java.util.Collection;

/**
 * 内部能力值作用对象
 * Created by Kun Yang on 16/2/15.
 */
public interface ProxyCapacityGoal extends CapacityGoal {

    CapacityGoal capacityGoal();

    @Override
    default long getID() {
        return capacityGoal().getID();
    }

    @Override
    default CapacityGoalType getGoalType() {
        return capacityGoal().getGoalType();
    }

    @Override
    default Collection<CapacitySupplier> suppliers() {
        return capacityGoal().suppliers();
    }

}
