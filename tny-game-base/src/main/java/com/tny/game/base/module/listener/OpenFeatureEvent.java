package com.tny.game.base.module.listener;

import com.tny.game.base.module.Feature;
import com.tny.game.base.module.FeatureExplorer;
import com.tny.game.event.Event;

public class OpenFeatureEvent extends Event<FeatureExplorer> {

    private Feature feature;

    public static void dispatch(FeatureExplorer source, Feature feature) {
        new OpenFeatureEvent(source, feature).dispatch();
    }

    private OpenFeatureEvent(FeatureExplorer source, Feature feature) {
        super("openFeature", source);
        this.feature = feature;
    }

    public Feature getOpenFeature() {
        return this.feature;
    }

}
