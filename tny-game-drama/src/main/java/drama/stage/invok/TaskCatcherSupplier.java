package drama.stage.invok;


import drama.stage.TaskContext;

/**
 * Created by Kun Yang on 16/1/23.
 */
public interface TaskCatcherSupplier<R> {

    R catchThrow(Throwable cause, TaskContext context);

}
