package com.tny.game.actor;

import com.tny.game.suite.base.Done;

@FunctionalInterface
public interface DoneSupplier<T> extends Completable {

    Done<T> getDone();

    @Override
    default boolean isCompleted() {
        return this.getDone().isSuccess();
    }

}
