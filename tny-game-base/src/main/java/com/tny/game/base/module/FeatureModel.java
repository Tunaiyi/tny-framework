package com.tny.game.base.module;

import com.tny.game.base.item.Model;

public interface FeatureModel extends Model {

    Feature getFeature();

    OpenMode getOpenMode();

    int getOpenLevel();

    boolean isCanOpen(FeatureExplorer explorer, OpenMode openMode);

    boolean isEffect();

    int getPriority();

}
