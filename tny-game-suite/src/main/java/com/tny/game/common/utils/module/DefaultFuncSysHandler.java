package com.tny.game.common.utils.module;

import com.tny.game.base.module.Feature;
import com.tny.game.base.module.FeatureExplorer;

public class DefaultFuncSysHandler extends GameFeatureHandler<Feature> {

    public DefaultFuncSysHandler(Feature feature) {
        super(feature);
    }

    @Override
    public boolean openFeature(FeatureExplorer explorer) {
        return true;
    }

}
