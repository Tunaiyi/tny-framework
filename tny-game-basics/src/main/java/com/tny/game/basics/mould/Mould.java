package com.tny.game.basics.mould;

import com.tny.game.common.enums.*;

public interface Mould extends IntEnumerable {

    @Override
    String name();

    boolean isValid();

    boolean isHasHandler();

}
