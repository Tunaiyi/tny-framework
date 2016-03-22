package drama.stage.invok;


import drama.stage.TaskContext;

/**
 * Created by Kun Yang on 16/1/23.
 */
@FunctionalInterface
public interface TaskSupplyDone<R> {

    R handle(boolean success, Throwable cause, TaskContext context);

}