package com.tny.game.common.utils.module;

import com.tny.game.base.module.Feature;

/**
 * Created by Kun Yang on 16/1/29.
 */
public interface GameFeature extends Feature {

    default void registerSelf() {
        Features.register(this);
    }

}
