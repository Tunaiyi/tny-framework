package com.tny.game.basics.mould;

/**
 * 开启条件
 * Created by Kun Yang on 16/6/20.
 */
public interface FeatureOpenModeCondition<FM extends FeatureModel, C> {

	boolean check(FeatureLauncher explorer, FM model, FeatureOpenMode<?> mode, C context);

}
