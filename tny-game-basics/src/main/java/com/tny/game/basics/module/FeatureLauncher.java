package com.tny.game.basics.module;

import java.util.Set;

public interface FeatureLauncher {

	long getPlayerId();

	int getLevel();

	boolean isModuleOpened(Moduler moduleType);

	Set<Moduler> getOpenedModules();

	boolean isFeatureOpened(Feature feature);

	Set<Feature> getOpenedFeatures();

}
