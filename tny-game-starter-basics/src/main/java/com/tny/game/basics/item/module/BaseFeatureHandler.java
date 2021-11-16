package com.tny.game.basics.item.module;

import com.tny.game.basics.module.*;

public abstract class BaseFeatureHandler<F extends Feature> implements FeatureHandler {

	private final F feature;

	public BaseFeatureHandler(F feature) {
		super();
		this.feature = feature;
	}

	@Override
	public F getFeature() {
		return this.feature;
	}

	@Override
	public String toString() {
		return "FeatureHandler [feature=" + this.feature + "]";
	}

}
