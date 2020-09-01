package com.tny.game.common.enums;

import com.tny.game.common.utils.ConfigChecker;

/**
 * Created by Kun Yang on 16/1/29.
 */

public interface EnumIdentifiable<ID> {

    ID getId();

    default void checkEnum() {
        ID id = getId();
        ConfigChecker.check(this.getClass(), id, "{}-[{}-ID:{}]发生重复", this.getClass(), this, id);
    }

}
