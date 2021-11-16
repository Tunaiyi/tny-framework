package com.tny.game.basics.module;

import com.tny.game.common.enums.*;

import java.util.*;

public interface Feature extends EnumIdentifiable<Integer> {

	String name();

	Collection<Moduler> dependModules();

	String getDesc();

	boolean isValid();

	boolean isHasHandler();

	Optional<Feature> getParent();

}
