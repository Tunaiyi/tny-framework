package com.tny.game.base.module.listener;

import com.tny.game.base.module.Feature;
import com.tny.game.base.module.FeatureExplorer;
import com.tny.game.event.BindP1EventBus;
import com.tny.game.event.EventBuses;

public interface FeatureListener {

    BindP1EventBus<FeatureListener, FeatureExplorer, Feature> OPEN_FEATURE_EVENT = EventBuses.of(FeatureListener.class,
            FeatureListener::handleOpenFeature);

    void handleOpenFeature(FeatureExplorer explorer, Feature openedFeature);

}