package com.tny.game.suite.base.module;

import com.google.common.collect.*;
import com.tny.game.base.item.xml.XMLModel;
import com.tny.game.base.module.*;
import com.tny.game.common.utils.ObjectAide;
import com.tny.game.common.utils.version.Version;

import java.util.*;
import java.util.stream.Collectors;

public class GameFeatureModel extends XMLModel implements FeatureModel {

    private int id;

    private String alias;

    private String desc;

    private Feature feature;

    private int openLevel;

    private Version openVersion;

    private int priority;

    private Set<OpenPlan> openPlans;

    private Map<OpenMode<?>, OpenPlan> openPlanMap;

    private boolean effect;

    @Override
    protected void doInit() {
        if (openPlans == null)
            openPlans = ImmutableSet.of();
        else
            openPlans = ImmutableSet.copyOf(this.openPlans);
        openPlanMap = ImmutableMap.copyOf(
                this.openPlans.stream().collect(Collectors.toMap(OpenPlan::getMode, ObjectAide::self)));
    }

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

    @Override
    public Optional<Version> getOpenVersion() {
        return Optional.ofNullable(openVersion);
    }

    @Override
    public Feature getFeature() {
        return this.feature;
    }

    @Override
    public Collection<OpenPlan> getOpenPlan() {
        return this.openPlans;
    }

    @Override
    public int getOpenLevel(OpenMode<?> mode) {
        OpenPlan plan = this.openPlanMap.get(mode);
        if (plan == null)
            return Integer.MAX_VALUE;
        return plan.getLevel();
    }

    @Override
    public boolean isCanOpen(FeatureExplorer explorer, OpenMode openMode) {
        return !explorer.isFeatureOpened(this.feature) && explorer.getLevel() >= this.openLevel && this.openPlans == openMode;
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
