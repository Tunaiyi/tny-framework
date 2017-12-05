package com.tny.game.common.utils;

import com.tny.game.base.item.behavior.Action;

/**
 * Created by Kun Yang on 16/1/28.
 */
public interface GameAction extends Action, RegisterSelf {

    @Override
    default void registerSelf() {
        Actions.register(this);
    }

}
