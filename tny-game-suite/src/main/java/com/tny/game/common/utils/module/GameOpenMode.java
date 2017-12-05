package com.tny.game.common.utils.module;

import com.tny.game.base.module.FeatureModel;
import com.tny.game.base.module.OpenMode;
import com.tny.game.common.utils.RegisterSelf;

/**
 * Created by Kun Yang on 16/1/29.
 */
public interface GameOpenMode<FM extends FeatureModel> extends OpenMode<FM>, RegisterSelf {

    @Override
    default void registerSelf() {
        OpenModes.register(this);
    }
}
