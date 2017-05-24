package com.tny.game.suite.base.capacity;

import com.tny.game.common.enums.EnumID;
import com.tny.game.suite.base.RegisterSelf;

import java.util.Set;

/**
 * 能力值提供器类型
 * Created by Kun Yang on 16/2/15.
 */
public interface CapacitySupplyType extends EnumID<Integer>, RegisterSelf {

    Set<CapacityGoalType> getGoalTypes();

    boolean isAll();

    @Override
    default void registerSelf() {
        CapacitySupplyTypes.register(this);
    }

}
