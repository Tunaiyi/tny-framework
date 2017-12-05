package com.tny.game.common.utils.module;

import com.tny.game.base.module.Module;
import com.tny.game.common.utils.RegisterSelf;

/**
 * Created by Kun Yang on 16/1/29.
 */
public interface GameModule extends Module, RegisterSelf {

    @Override
    default void registerSelf() {
        Modules.register(this);
    }

}
