package com.tny.game.base.module;

import com.tny.game.common.enums.EnumID;

public interface OpenMode<FM extends FeatureModel> extends EnumID<Integer> {

    String name();

    boolean check(FeatureExplorer explorer, FM model, Object context);

}
