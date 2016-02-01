package com.tny.game.base.module;

import com.tny.game.common.enums.EnumID;

public interface Module extends EnumID<Integer> {

    String name();

    boolean isValid();

    boolean isHasHandler();

}
