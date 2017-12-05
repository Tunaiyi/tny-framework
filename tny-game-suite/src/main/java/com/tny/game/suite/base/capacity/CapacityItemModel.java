package com.tny.game.suite.base.capacity;

import com.tny.game.base.item.ItemModel;

import java.util.Set;

/**
 * Created by Kun Yang on 2017/7/24.
 */
public interface CapacityItemModel extends ItemModel {

    Set<Capacity> getCapacities();

    Set<CapacityGroup> getCapacityGroups();

}
