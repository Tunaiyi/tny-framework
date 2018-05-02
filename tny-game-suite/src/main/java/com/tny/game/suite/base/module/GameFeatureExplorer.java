package com.tny.game.suite.base.module;

import com.google.common.collect.ImmutableSet;
import com.tny.game.base.module.*;
import com.tny.game.common.utils.Throws;

import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 默认的功能开放管理器
 * Created by Kun Yang on 16/1/29.
 */
public abstract class GameFeatureExplorer implements FeatureExplorer {

    protected AtomicReference<Set<Module>> openedModules;

    protected AtomicReference<Set<Feature>> openedFeatures;

    protected GameFeatureExplorer() {
        this(ImmutableSet.of(), ImmutableSet.of());
    }

    protected GameFeatureExplorer(Set<Feature> openedFeatures, Set<Module> openedModules) {
        Throws.checkNotNull(openedFeatures);
        Throws.checkNotNull(openedModules);
        this.openedModules = new AtomicReference<>(ImmutableSet.copyOf(openedModules));
        this.openedFeatures = new AtomicReference<>(ImmutableSet.copyOf(openedFeatures));
    }

    protected <T> boolean open(AtomicReference<Set<T>> ref, T value) {
        Set<T> set = ref.get();
        if (set.contains(value))
            return false;
        while (true) {
            Set<T> newSet = ImmutableSet.<T>builder()
                    .addAll(set)
                    .add(value)
                    .build();
            if (ref.compareAndSet(set, newSet))
                return true;
        }

    }

    protected boolean open(FeatureModel model) {
        if (open(openedFeatures, model.getFeature())) {
            this.doOpen(model);
            return true;
        }
        return false;
    }

    protected boolean open(Module module) {
        return open(openedModules, module);
    }

    protected void doOpen(FeatureModel model) {
    }

    @Override
    public boolean isModuleOpened(Module moduleType) {
        return openedModules.get().contains(moduleType);
    }

    @Override
    public Set<Module> getOpenedModules() {
        return openedModules.get();
    }

    @Override
    public boolean isFeatureOpened(Feature feature) {
        return openedFeatures.get().contains(feature);
    }

    @Override
    public Set<Feature> getOpenedFeatures() {
        return openedFeatures.get();
    }
}
