package com.tny.game.base.module;

import com.tny.game.common.enums.EnumIdentifiable;

import java.util.*;

public interface Feature extends EnumIdentifiable<Integer> {

    String name();

    Collection<Module> dependModules();

    String getDesc();

    boolean isValid();

    boolean isHasHandler();

    Optional<Feature> getParent();
}
