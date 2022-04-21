package com.tny.game.common.enums;

/**
 * Created by Kun Yang on 16/1/29.
 */

public interface IntEnumerable extends Enumerable<Integer> {

    int id();

    @Override
    default Integer getId() {
        return id();
    }

}
