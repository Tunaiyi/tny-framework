package com.tny.game.basics.item.module;

import com.tny.game.basics.module.*;

public class DefaultModulerHandler extends BaseModulerHandler<Moduler, Object> {

	public DefaultModulerHandler(Moduler moduleType) {
		super(moduleType);
	}

	@Override
	public boolean openModule(FeatureLauncher launcher) {
		return true;
	}

	@Override
	public void loadModule(FeatureLauncher launcher) {

	}

	@Override
	public Object loadContext(FeatureLauncher launcher, Object o) {
		return null;
	}

}
