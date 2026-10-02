/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.basics.item.mould;

import com.google.common.collect.ImmutableSet;
import com.tny.game.basics.mould.*;
import com.tny.game.common.utils.*;

import java.util.Set;
import java.util.function.Consumer;

/**
 * 默认的功能开放管理器
 * Created by Kun Yang on 16/1/29.
 */
public abstract class GameFeatureLauncher implements FeatureLauncher {

    private Set<Mould> openedMoulds;

    private Set<Feature> openedFeatures;

    protected GameFeatureLauncher() {
        this(ImmutableSet.of(), ImmutableSet.of());
    }

    protected GameFeatureLauncher(Set<Feature> openedFeatures, Set<Mould> openedMoulds) {
        Asserts.checkNotNull(openedFeatures);
        Asserts.checkNotNull(openedMoulds);
        this.openedMoulds = ImmutableSet.copyOf(openedMoulds);
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

    protected boolean openMould(Mould mould, MouldHandler handler) {
        if (open(this.openedMoulds, mould, this::setOpenedMoulds)) {
            handler.openMould(this);
            doOpenMould(mould);
            return true;
        }
        return false;
    }

    protected void doOpenFeature(FeatureModel model) {
    }

    protected void doOpenMould(Mould mould) {
    }

    @Override
    public boolean isMouldOpened(Mould mould) {
        return this.openedMoulds.contains(mould);
    }

    @Override
    public Set<Mould> getOpenedMoulds() {
        return this.openedMoulds;
    }

    @Override
    public boolean isFeatureOpened(Feature feature) {
        return this.openedFeatures.contains(feature);
    }

    @Override
    public Set<Feature> getOpenedFeatures() {
        return this.openedFeatures;
    }

    protected GameFeatureLauncher setOpenedMoulds(Set<Mould> openedMoulds) {
        this.openedMoulds = ImmutableSet.copyOf(openedMoulds);
        return this;
    }

    protected GameFeatureLauncher setOpenedFeatures(Set<Feature> openedFeatures) {
        this.openedFeatures = ImmutableSet.copyOf(openedFeatures);
        return this;
    }

}
