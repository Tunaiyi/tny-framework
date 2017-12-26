package com.tny.game.base.module;

import com.tny.game.common.utils.version.*;

import java.util.*;

public interface FeatureExplorer {

    long getPlayerID();

    int getLevel();

    //TODO 迁移到其他地方
    Optional<Version> getFeatureVersion();

    boolean isModuleOpened(Module moduleType);

    Set<Module> getOpenedModules();

    boolean isFeatureOpened(Feature feature);

    Set<Feature> getOpenedFeatures();

}
