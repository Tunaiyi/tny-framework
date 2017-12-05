package com.tny.game.common.utils;

import com.tny.game.base.item.behavior.DemandType;

/**
 * Created by Kun Yang on 16/1/28.
 */
public interface GameDemandType extends DemandType, RegisterSelf {

    @Override
    default void registerSelf() {
        DemandTypes.register(this);
    }

}
