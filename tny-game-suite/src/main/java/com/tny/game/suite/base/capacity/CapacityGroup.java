package com.tny.game.suite.base.capacity;

import com.tny.game.common.enums.EnumIdentifiable;

import java.util.List;

/**
 * Created by Kun Yang on 2017/4/7.
 */
public interface CapacityGroup extends EnumIdentifiable<Integer> {

    List<Capacity> getCapacities();

    default void registerSelf() {
        Capacities.register(this);
    }

}
