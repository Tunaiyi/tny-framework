package com.tny.game.basics.item.module;

import com.tny.game.basics.module.*;

public abstract class BaseModulerHandler<M extends Moduler, C> implements ModulerHandler {

	private final M moduleType;

	public BaseModulerHandler(M moduleType) {
		super();
		this.moduleType = moduleType;
	}

	@Override
	public Moduler getModule() {
		return this.moduleType;
	}

	public abstract C loadContext(FeatureLauncher launcher, C context);

	@Override
	public void removeModule(FeatureLauncher launcher) {
	}

	@Override
	public String toString() {
		return "GameModule [getModuleType()=" + this.getModule() + "]";
	}

}
