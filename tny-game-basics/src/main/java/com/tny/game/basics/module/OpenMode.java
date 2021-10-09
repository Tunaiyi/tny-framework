package com.tny.game.basics.module;

import com.tny.game.common.enums.*;

public interface OpenMode<FM extends FeatureModel> extends EnumIdentifiable<Integer> {

    String name();

    boolean check(FeatureExplorer explorer, FM model, Object context);

}
