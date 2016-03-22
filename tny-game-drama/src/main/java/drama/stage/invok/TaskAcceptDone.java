package drama.stage.invok;


import drama.stage.TaskContext;

/**
 * Created by Kun Yang on 16/1/23.
 */
@FunctionalInterface
public interface TaskAcceptDone<T> {

    void handle(boolean success, T value, Throwable cause, TaskContext context);

}
