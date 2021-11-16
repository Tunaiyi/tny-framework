package com.tny.game.basics.module;

public interface FeatureHandler {

	/**
	 * 系统开启
	 *
	 * @return
	 */
	Feature getFeature();

	/**
	 * 开启模块
	 *
	 * @param explorer
	 * @return
	 */
	boolean openFeature(FeatureLauncher explorer);

	default void loadFeature(FeatureLauncher explorer) {
	}

}
