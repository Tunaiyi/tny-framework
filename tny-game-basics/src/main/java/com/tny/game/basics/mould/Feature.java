package com.tny.game.basics.mould;

import com.tny.game.common.enums.*;

import java.util.Collection;

public interface Feature extends IntEnumerable {

    @Override
    String name();

    Collection<Mould> dependMoulds();

    String getDesc();

    boolean isValid();

    boolean isHasHandler();

}
