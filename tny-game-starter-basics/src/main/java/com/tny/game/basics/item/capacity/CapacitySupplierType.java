package com.tny.game.basics.item.capacity;

import com.tny.game.common.enums.*;

/**
 * 能力值提供器类型
 * Created by Kun Yang on 16/2/15.
 */
@FunctionalInterface
public interface CapacitySupplierType extends IntEnumerable {

    CapacitySupplierType NONE = () -> 0;

}
