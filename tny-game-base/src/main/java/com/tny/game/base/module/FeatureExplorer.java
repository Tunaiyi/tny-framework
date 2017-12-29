package com.tny.game.base.module;

import java.util.*;

public interface FeatureExplorer {

    long getPlayerID();

    int getLevel();

    boolean isModuleOpened(Module moduleType);

    Set<Module> getOpenedModules();

    boolean isFeatureOpened(Feature feature);

    Set<Feature> getOpenedFeatures();

}
