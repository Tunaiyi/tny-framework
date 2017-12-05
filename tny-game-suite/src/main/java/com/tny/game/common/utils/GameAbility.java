package com.tny.game.common.utils;

import com.tny.game.base.item.Ability;

/**
 * Created by Kun Yang on 16/1/28.
 */
public interface GameAbility extends Ability {

    default void registerSelf() {
        Abilities.register(this);
    }

}
