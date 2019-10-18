package com.tny.game.base.module;

import com.tny.game.common.enums.EnumIdentifiable;

public interface OpenMode<FM extends FeatureModel> extends EnumIdentifiable<Integer> {

    String name();

    boolean check(FeatureExplorer explorer, FM model, Object context);

}
