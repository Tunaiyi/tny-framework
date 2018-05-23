package com.tny.game.suite.base.capacity;

import com.tny.game.suite.base.RegisterSelf;
import com.tny.game.common.enums.EnumID;

/**
 * 能力值提供器类型
 * Created by Kun Yang on 16/2/15.
 */
public interface CapacitySupplierType extends EnumID<Integer>, RegisterSelf {

    @Override
    default void registerSelf() {
        CapacitySupplierTypes.register(this);
    }

}