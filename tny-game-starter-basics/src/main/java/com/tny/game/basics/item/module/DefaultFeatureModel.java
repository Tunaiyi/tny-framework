package com.tny.game.basics.item.module;

import com.google.common.collect.*;
import com.tny.game.basics.item.model.*;
import com.tny.game.basics.module.*;
import com.tny.game.common.utils.*;
import com.tny.game.common.version.*;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultFeatureModel extends BaseModel<Object> implements FeatureModel {

	private int id;

	private String alias;

	private String desc;

	private Feature feature;

	private int openLevel;

	private Version openVersion;

	private int priority;

	private Set<FeatureOpenPlan> openPlans;

	private Map<FeatureOpenMode<?>, FeatureOpenPlan> openPlanMap;

	private boolean effect;

	@Override
	protected void doInit(Object context) {
		if (this.openPlans == null) {
			this.openPlans = ImmutableSet.of();
		} else {
			this.openPlans = ImmutableSet.copyOf(this.openPlans);
		}
		this.openPlanMap = ImmutableMap.copyOf(
				this.openPlans.stream().collect(Collectors.toMap(FeatureOpenPlan::getMode, ObjectAide::self)));
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getAlias() {
		return this.alias;
	}

	@Override
	public String getDesc() {
		return this.desc;
	}

	@Override
	public Optional<Feature> getParent() {
		return this.feature.getParent();
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
		return Optional.ofNullable(this.openVersion);
	}

	@Override
	public Feature getFeature() {
		return this.feature;
	}

	@Override
	public Collection<FeatureOpenPlan> getOpenPlan() {
		return this.openPlans;
	}

	@Override
	public int getOpenLevel(FeatureOpenMode<?> mode) {
		FeatureOpenPlan plan = this.openPlanMap.get(mode);
		if (plan == null) {
			return Integer.MAX_VALUE;
		}
		return plan.getLevel();
	}

	@Override
	public boolean isCanOpen(FeatureLauncher launcher, FeatureOpenMode<?> openMode) {
		return !launcher.isFeatureOpened(this.feature) && launcher.getLevel() >= this.openLevel && this.openPlans == openMode;
	}

	@Override
	public String toString() {
		return "GameFeatureModel{" +
				"feature=" + this.feature +
				", id=" + this.id +
				", desc='" + this.desc + '\'' +
				", effect=" + this.effect +
				'}';
	}

}
