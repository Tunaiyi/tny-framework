package com.tny.game.basics.item.module;

import com.tny.game.basics.module.*;

public class DefaultFuncSysHandler extends BaseFeatureHandler<Feature> {

	public DefaultFuncSysHandler(Feature feature) {
		super(feature);
	}

	@Override
	public boolean openFeature(FeatureLauncher launcher) {
		return true;
	}

}
