package com.tny.game.base.module.listener;

import com.tny.game.base.module.*;
import com.tny.game.common.event.*;

public interface FeatureListener {

    BindP1EventBus<FeatureListener, FeatureExplorer, Feature> OPEN_FEATURE_EVENT = EventBuses.of(FeatureListener.class,
            FeatureListener::handleOpenFeature);

    void handleOpenFeature(FeatureExplorer explorer, Feature openedFeature);

}