package com.tny.game.basics.module.listener;

import com.tny.game.basics.module.*;
import com.tny.game.common.event.bus.*;

public interface FeatureListener {

    BindP1EventBus<FeatureListener, FeatureExplorer, Feature> OPEN_FEATURE_EVENT = EventBuses.of(FeatureListener.class,
            FeatureListener::handleOpenFeature);

    void handleOpenFeature(FeatureExplorer explorer, Feature openedFeature);

}