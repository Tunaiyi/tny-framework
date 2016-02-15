package com.tny.game.actor.task.invok;

import com.tny.game.actor.task.TaskContext;

/**
 * Created by Kun Yang on 16/1/23.
 */
@FunctionalInterface
public interface TaskAcceptDone<T> {

    void handle(boolean success, T value, Throwable cause, TaskContext context);

}
