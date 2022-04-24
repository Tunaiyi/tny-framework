package com.tny.game.basics.item.capacity;

import com.tny.game.basics.item.*;

import java.util.Set;

/**
 * Created by Kun Yang on 2017/7/24.
 */
public interface CapacitySupplierItemModel extends ItemModel {

    /**
     * @return 提供器类型
     */
    CapacitySupplierType getSupplierType();

    /**
     * @return 能力值类型
     */
    Set<Capacity> getCapacities();

    /**
     * @return 能力值组
     */
    Set<CapacityGroup> getCapacityGroups();

}
