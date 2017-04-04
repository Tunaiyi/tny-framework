package com.tny.game.suite.base.capacity;

import com.tny.game.common.enums.EnumID;

import java.util.Set;

/**
 * 能力值提供器类型
 * Created by Kun Yang on 16/2/15.
 */
public interface CapacitySupplyType extends EnumID<Integer>{

    Set<CapacityGoalType> getGoalTypes();

    boolean isAll();

}
