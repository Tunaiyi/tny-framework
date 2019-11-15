package com.tny.game.suite.base;

import com.tny.game.base.item.behavior.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
public interface GameDemandType extends DemandType, RegisterSelf {

    @Override
    default void registerSelf() {
        DemandTypes.register(this);
    }

}
