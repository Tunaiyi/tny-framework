package com.tny.game.common.utils;

import com.tny.game.base.item.behavior.DemandParam;

/**
 * Created by Kun Yang on 16/1/28.
 */
public interface GameDemandParam extends DemandParam, RegisterSelf {

    @Override
    default void registerSelf() {
        DemandParams.register(this);
    }
}
