package com.tny.game.suite.base.module;

import com.tny.game.base.module.*;
import com.tny.game.suite.base.*;

/**
 * Created by Kun Yang on 16/1/29.
 */
public interface GameModule extends Module, RegisterSelf {

    @Override
    default void registerSelf() {
        Modules.register(this);
    }

}
