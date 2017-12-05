package com.tny.game.common.utils.capacity;

import com.tny.game.common.enums.EnumID;

import java.util.List;

/**
 * Created by Kun Yang on 2017/4/7.
 */
public interface CapacityGroup extends EnumID<Integer> {

    List<Capacity> getCapacities();

}
