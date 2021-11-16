package com.tny.game.basics.item.module;

import com.google.common.collect.ImmutableSet;
import com.tny.game.basics.module.*;
import com.tny.game.common.utils.*;

import java.util.Set;
import java.util.function.Consumer;

/**
 * 默认的功能开放管理器
 * Created by Kun Yang on 16/1/29.
 */
public abstract class GameFeatureLauncher implements FeatureLauncher {

	private Set<Moduler> openedModules;

	private Set<Feature> openedFeatures;

	protected GameFeatureLauncher() {
		this(ImmutableSet.of(), ImmutableSet.of());
	}

	protected GameFeatureLauncher(Set<Feature> openedFeatures, Set<Moduler> openedModules) {
		Asserts.checkNotNull(openedFeatures);
		Asserts.checkNotNull(openedModules);
		this.openedModules = ImmutableSet.copyOf(openedModules);
		this.openedFeatures = ImmutableSet.copyOf(openedFeatures);
	}

	protected <T> boolean open(Set<T> set, T value, Consumer<Set<T>> setter) {
		if (set.contains(value)) {
			return false;
		}
		synchronized (this) {
			if (set.contains(value)) {
				return false;
			}
			setter.accept(ImmutableSet.<T>builder()
					.addAll(set)
					.add(value)
					.build());
			return true;
		}
	}

	protected boolean openFeature(FeatureModel model, FeatureHandler handler) {
		if (open(this.openedFeatures, model.getFeature(), this::setOpenedFeatures)) {
			handler.openFeature(this);
			this.doOpenFeature(model);
			return true;
		}
		return false;
	}

	protected boolean openModuler(Moduler module, ModulerHandler handler) {
		if (open(this.openedModules, module, this::setOpenedModules)) {
			handler.openModule(this);
			doOpenModuler(module);
			return true;
		}
		return false;
	}

	protected void doOpenFeature(FeatureModel model) {
	}

	protected void doOpenModuler(Moduler module) {
	}

	@Override
	public boolean isModuleOpened(Moduler moduleType) {
		return this.openedModules.contains(moduleType);
	}

	@Override
	public Set<Moduler> getOpenedModules() {
		return this.openedModules;
	}

	@Override
	public boolean isFeatureOpened(Feature feature) {
		return this.openedFeatures.contains(feature);
	}

	@Override
	public Set<Feature> getOpenedFeatures() {
		return this.openedFeatures;
	}

	protected GameFeatureLauncher setOpenedModules(Set<Moduler> openedModules) {
		this.openedModules = ImmutableSet.copyOf(openedModules);
		return this;
	}

	protected GameFeatureLauncher setOpenedFeatures(Set<Feature> openedFeatures) {
		this.openedFeatures = ImmutableSet.copyOf(openedFeatures);
		return this;
	}

}
