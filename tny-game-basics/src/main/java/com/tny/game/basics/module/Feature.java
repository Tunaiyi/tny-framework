package com.tny.game.basics.module;

import com.tny.game.common.enums.*;

import java.util.Collection;

public interface Feature extends IntEnumerable {

	String name();

	Collection<Moduler> dependModules();

	String getDesc();

	boolean isValid();

	boolean isHasHandler();

}
