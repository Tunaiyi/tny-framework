package com.tny.game.basics.mould;

import com.google.common.collect.ImmutableSet;
import com.tny.game.basics.item.*;
import com.tny.game.common.version.*;

import java.util.*;

public interface FeatureModel extends Model {

    Feature getFeature();

    Optional<Feature> getParent();

    Optional<Version> getOpenVersion();

    Collection<FeatureOpenPlan> getOpenPlan();

    int getOpenLevel(FeatureOpenMode<?> mode);

    boolean isCanOpen(FeatureLauncher explorer, FeatureOpenMode<?> openMode);

    boolean isEffect();

    int getPriority();

    @Override
    default Set<Object> tags() {
        return ImmutableSet.of();
    }

}
