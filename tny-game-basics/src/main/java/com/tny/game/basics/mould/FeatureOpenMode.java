package com.tny.game.basics.mould;

import com.tny.game.common.enums.*;

public interface FeatureOpenMode<FM extends FeatureModel> extends IntEnumerable {

    String name();

    boolean check(FeatureLauncher explorer, FM model, Object context);

}
