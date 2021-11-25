package com.tny.game.basics.module;

import com.tny.game.common.enums.*;

public interface FeatureOpenMode<FM extends FeatureModel> extends EnumIdentifiable<Integer> {

	@Override
	Integer getId();

	String name();

	boolean check(FeatureLauncher explorer, FM model, Object context);

}
