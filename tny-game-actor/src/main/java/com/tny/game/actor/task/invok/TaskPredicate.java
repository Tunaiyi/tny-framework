package com.tny.game.actor.task.invok;

import com.tny.game.actor.task.TaskContext;

/**
 * Created by Kun Yang on 16/1/22.
 */
@FunctionalInterface
public interface TaskPredicate<T> {

    boolean test(T value, TaskContext context);

}
