package com.tny.game.base.module;

import com.google.common.collect.ImmutableSet;
import com.tny.game.base.item.Model;
import com.tny.game.common.utils.version.*;

import java.util.*;

public interface FeatureModel extends Model {

    Feature getFeature();

    Optional<Feature> getParent();

    Optional<Version> getOpenVersion();

    Collection<OpenPlan> getOpenPlan();

    int getOpenLevel(OpenMode<?> mode);

    boolean isCanOpen(FeatureExplorer explorer, OpenMode openMode);

    boolean isEffect();

    int getPriority();

    @Override
    default Set<Object> tags() {
        return ImmutableSet.of();
    }

}
