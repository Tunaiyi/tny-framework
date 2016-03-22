package com.tny.game.base.module;

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
    boolean openFeature(FeatureExplorer explorer);

}
