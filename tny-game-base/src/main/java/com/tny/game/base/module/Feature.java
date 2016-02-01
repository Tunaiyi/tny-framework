package com.tny.game.base.module;

import com.tny.game.common.enums.EnumID;

import java.util.Collection;

public interface Feature extends EnumID<Integer> {

    String name();

    Collection<Module> dependModules();

    String getDesc();

    boolean isValid();

    boolean isHasHandler();

}
