package com.tny.game.suite.base.module;

import com.tny.game.basics.mould.*;

public class DefaultFuncSysHandler extends GameFeatureHandler<Feature> {

    public DefaultFuncSysHandler(Feature feature) {
        super(feature);
    }

    @Override
    public boolean openFeature(FeatureExplorer explorer) {
        return true;
    }

}
