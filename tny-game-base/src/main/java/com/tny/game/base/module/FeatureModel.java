package com.tny.game.base.module;

import com.tny.game.base.item.Model;

public interface FeatureModel extends Model, Comparable<FeatureModel> {

    Feature getFeature();

    OpenMode<?> getOpenMode();

    int getOpenLevel();

    boolean isCanOpen(FeatureExplorer explorer, OpenMode openMode);

    boolean isEffect();

    int getPriority();

    @Override
    default int compareTo(FeatureModel other) {
        int openModelComp;
        if ((openModelComp = this.getOpenMode().getID() - other.getOpenMode().getID()) != 0)
            return openModelComp;
        int levelComp;
        if ((levelComp = this.getOpenLevel() - other.getOpenLevel()) != 0)
            return levelComp;
        int proComp;
        if ((proComp = this.getPriority() - other.getPriority()) != 0)
            return proComp;
        return other.getID() - this.getID();
    }

}
