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
     * @param explorer
     * @return
     */
    boolean openModule(FeatureExplorer explorer);

    /**
     * 加载模块
     *
     * @param explorer
     */
    void loadModule(FeatureExplorer explorer);

    /**
     * 删除模块
     *
     * @param explorer
     */
    void removeModule(FeatureExplorer explorer);

    //	/**
    //	 * 获取模块信息数据
    //	 *
    //	 * @param playerInfo
    //	 * @return
    //	 */
    //	public abstract D getModeleData(ModuleOwner moduleOwner);

}
