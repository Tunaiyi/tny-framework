package com.tny.game.basics.module;

import com.tny.game.common.enums.*;

public interface Moduler extends EnumIdentifiable<Integer> {

	String name();

	boolean isValid();

	boolean isHasHandler();

}
