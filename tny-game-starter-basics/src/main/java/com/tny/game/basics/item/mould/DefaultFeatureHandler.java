package com.tny.game.basics.item.mould;

import com.tny.game.basics.mould.*;

public class DefaultFeatureHandler extends BaseFeatureHandler<Feature> {

    public DefaultFeatureHandler(Feature feature) {
        super(feature);
    }

    @Override
    public boolean openFeature(FeatureLauncher launcher) {
        return true;
    }

}
