package com.tny.game.base.module;

public interface ModuleHandler {

    /**
     * 模块类型
     *
     * @return
     */
    Module getModule();

    /**
     * 开启模块
     *
     * @param featureExplorer
     * @return
     */
    boolean openModule(FeatureExplorer featureExplorer);

    /**
     * 加载模块
     *
     * @param featureExplorer
     */
    void loadModule(FeatureExplorer featureExplorer);

    /**
     * 删除模块
     *
     * @param featureExplorer
     */
    void removeModule(FeatureExplorer featureExplorer);

    //	/**
    //	 * 获取模块信息数据
    //	 *
    //	 * @param playerInfo
    //	 * @return
    //	 */
    //	public abstract D getModeleData(ModuleOwner moduleOwner);

}
