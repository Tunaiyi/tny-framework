package com.tny.game.actor.task.invok;

import com.tny.game.actor.task.TaskContext;

/**
 * Created by Kun Yang on 16/1/22.
 */
@FunctionalInterface
public interface TaskSupplier<R> {

    R get(TaskContext context);

}
