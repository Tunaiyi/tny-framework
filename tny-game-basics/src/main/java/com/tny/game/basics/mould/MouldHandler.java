package com.tny.game.basics.mould;

public interface MouldHandler {

	/**
	 * 模块类型
	 *
	 * @return
	 */
	Mould getMould();

	/**
	 * 开启模块
	 *
	 * @param launcher
	 * @return
	 */
	boolean openMould(FeatureLauncher launcher);

	/**
	 * 加载模块
	 *
	 * @param launcher
	 */
	void loadMould(FeatureLauncher launcher);

	/**
	 * 删除模块
	 *
	 * @param launcher
	 */
	void removeMould(FeatureLauncher launcher);

}
