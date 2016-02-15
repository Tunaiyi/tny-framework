package com.tny.game.actor.task.invok;

import com.tny.game.actor.task.TaskContext;

/**
 * Created by Kun Yang on 16/1/22.
 */
@FunctionalInterface
public interface TaskConsumer<T> {

    void accept(T value, TaskContext context);

}
