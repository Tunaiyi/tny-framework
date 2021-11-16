package com.tny.game.basics.module;

public interface ModulerHandler {

	/**
	 * 模块类型
	 *
	 * @return
	 */
	Moduler getModule();

	/**
	 * 开启模块
	 *
	 * @param explorer
	 * @return
	 */
	boolean openModule(FeatureLauncher explorer);

	/**
	 * 加载模块
	 *
	 * @param explorer
	 */
	void loadModule(FeatureLauncher explorer);

	/**
	 * 删除模块
	 *
	 * @param explorer
	 */
	void removeModule(FeatureLauncher explorer);

}
