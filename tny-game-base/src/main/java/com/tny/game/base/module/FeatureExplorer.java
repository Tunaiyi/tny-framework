package com.tny.game.base.module;

import java.util.Set;

public interface FeatureExplorer {

    long getPlayerId();

    int getLevel();

    boolean isModuleOpened(Module moduleType);

    Set<Module> getOpenedModules();

    boolean isFeatureOpened(Feature feature);

    Set<Feature> getOpenedFeatures();

}
