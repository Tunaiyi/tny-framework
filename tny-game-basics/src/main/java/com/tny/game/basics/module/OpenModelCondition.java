package com.tny.game.basics.module;

/**
 * 开启条件
 * Created by Kun Yang on 16/6/20.
 */
public interface OpenModelCondition<FM extends FeatureModel, C> {

    boolean check(FeatureExplorer explorer, FM model, OpenMode<?> mode, C context);

}
