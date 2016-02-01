package com.tny.game.suite.base.module;

import com.tny.game.base.module.OpenMode;
import com.tny.game.suite.base.RegisterSelf;

/**
 * Created by Kun Yang on 16/1/29.
 */
public interface GameOpenMode extends OpenMode, RegisterSelf {

    @Override
    default void registerSelf() {
        OpenModes.register(this);
    }
}
