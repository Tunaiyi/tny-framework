package com.tny.game.actor;

import com.tny.game.common.utils.Done;

@FunctionalInterface
public interface Available<T> {

    Done<T> achieve();

}
