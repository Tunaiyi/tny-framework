package com.tny.game.suite.base.module;

import com.tny.game.base.module.*;
import com.tny.game.common.utils.version.*;

import java.util.*;

public class GameFeatureModel implements FeatureModel {

    private int id;

    private String alias;

    private String desc;

    private Feature feature;

    private int openLevel;

    private Version openVersion;

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

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public Optional<Feature> getParent() {
        return feature.getParent();
    }

    @Override
    public boolean isEffect() {
        return this.effect;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    public Optional<Version> getOpenVersion() {
        return Optional.ofNullable(openVersion);
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
