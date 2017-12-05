package com.tny.game.common.utils.module;

import com.tny.game.base.module.Feature;
import com.tny.game.base.module.FeatureExplorer;
import com.tny.game.base.module.FeatureModel;
import com.tny.game.base.module.OpenMode;

public class GameFeatureModel implements FeatureModel {

    private int id;

    private String alias;

    private String desc;

    private Feature feature;

    private int openLevel;

    private int priority;

    private OpenMode openMode;

    private boolean effect;

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public boolean isEffect() {
        return this.effect;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }


    @Override
    public Feature getFeature() {
        return this.feature;
    }

    @Override
    public OpenMode getOpenMode() {
        return this.openMode;
    }

    @Override
    public int getOpenLevel() {
        return openLevel;
    }

    @Override
    public boolean isCanOpen(FeatureExplorer explorer, OpenMode openMode) {
        return !explorer.isFeatureOpened(this.feature) && explorer.getLevel() >= this.openLevel && this.openMode == openMode;
    }

    @Override
    public String toString() {
        return "GameFeatureModel{" +
                "feature=" + feature +
                ", id=" + id +
                ", desc='" + desc + '\'' +
                ", effect=" + effect +
                '}';
    }

}
