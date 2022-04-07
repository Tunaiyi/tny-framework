package com.tny.game.basics.item.mould;

import com.tny.game.basics.mould.*;

public class DefaultMouldHandler extends BaseMouldHandler<Mould, Object> {

	public DefaultMouldHandler(Mould mould) {
		super(mould);
	}

	@Override
	public boolean openMould(FeatureLauncher launcher) {
		return true;
	}

	@Override
	public void loadMould(FeatureLauncher launcher) {

	}

	@Override
	public void loadContext(FeatureLauncher launcher, Object o) {
	}

}
