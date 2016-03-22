package drama.stage.invok;


import drama.stage.TaskContext;

/**
 * Created by Kun Yang on 16/1/22.
 */
@FunctionalInterface
public interface TaskBooleanSupplier {

    boolean getAsBoolean(TaskContext context);

}
