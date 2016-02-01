package com.tny.game.suite.base;

import com.tny.game.base.item.behavior.Behavior;

/**
 * Created by Kun Yang on 16/1/28.
 */
public interface GameBehavior extends Behavior, RegisterSelf {

    @Override
    default void registerSelf() {
        Behaviors.register(this);
    }
}
