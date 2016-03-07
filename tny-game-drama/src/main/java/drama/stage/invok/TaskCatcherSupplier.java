package drama.stage.invok;

import com.tny.game.actor.task.TaskContext;

/**
 * Created by Kun Yang on 16/1/23.
 */
public interface TaskCatcherSupplier<R> {

    R catchThrow(Throwable cause, TaskContext context);

}
