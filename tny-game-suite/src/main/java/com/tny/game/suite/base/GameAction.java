package com.tny.game.suite.base;

import com.tny.game.base.item.behavior.*;

/**
 * Created by Kun Yang on 16/1/28.
 */
public interface GameAction extends Action, RegisterSelf {

    @Override
    default void registerSelf() {
        Actions.register(this);
    }

}
