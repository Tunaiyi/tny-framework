package com.tny.game.basics.module;

import com.tny.game.common.enums.*;

public interface Module extends EnumIdentifiable<Integer> {

    String name();

    boolean isValid();

    boolean isHasHandler();

}
