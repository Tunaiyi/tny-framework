package com.tny.game.actor.task.invok;

import com.tny.game.actor.task.TaskContext;

/**
 * Created by Kun Yang on 16/1/23.
 */
@FunctionalInterface
public interface TaskSupplyDone<R> {

    R handle(boolean success, Throwable cause, TaskContext context);

}