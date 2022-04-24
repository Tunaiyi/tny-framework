package com.tny.game.basics.mould.listener;

import com.tny.game.basics.mould.*;
import com.tny.game.common.event.bus.*;

public interface FeatureListener {

    BindP1EventBus<FeatureListener, FeatureLauncher, Feature> OPEN_FEATURE_EVENT = EventBuses.of(FeatureListener.class,
            FeatureListener::handleOpenFeature);

    void handleOpenFeature(FeatureLauncher explorer, Feature openedFeature);

}