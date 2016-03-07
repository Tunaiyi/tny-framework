package drama.stage.invok;

import com.tny.game.actor.task.TaskContext;

/**
 * Created by Kun Yang on 16/1/23.
 */
@FunctionalInterface
public interface TaskApplyDone<V, R> {

    R handle(boolean success, V value, Throwable cause, TaskContext context);

}