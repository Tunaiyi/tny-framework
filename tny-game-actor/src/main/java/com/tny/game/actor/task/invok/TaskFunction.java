package com.tny.game.actor.task.invok;

import com.tny.game.actor.task.TaskContext;

/**
 * Created by Kun Yang on 16/1/22.
 */
@FunctionalInterface
public interface TaskFunction<T, R> {

    R apply(T value, TaskContext context);

}
