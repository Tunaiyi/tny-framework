package drama.stage.invok;

import com.tny.game.actor.task.TaskContext;

/**
 * Created by Kun Yang on 16/1/22.
 */
@FunctionalInterface
public interface TaskBooleanSupplier {

    boolean getAsBoolean(TaskContext context);

}
