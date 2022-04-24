package com.tny.game.basics.mould;

import java.util.Set;

public interface FeatureLauncher {

    long getPlayerId();

    int getLevel();

    boolean isMouldOpened(Mould mould);

    Set<Mould> getOpenedMoulds();

    boolean isFeatureOpened(Feature feature);

    Set<Feature> getOpenedFeatures();

}
