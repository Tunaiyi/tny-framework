package com.tny.game.actor.system;

/**
 * Created by Kun Yang on 16/1/21.
 */
@FunctionalInterface
public interface TaskExecutor {

    void execute(Runnable runnable);

    default void reportFailure(Throwable cause) {
    }

    default TaskExecutor prepare() {
        return this;
    }

}
