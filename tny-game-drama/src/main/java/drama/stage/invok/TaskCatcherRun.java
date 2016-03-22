package drama.stage.invok;


import drama.stage.TaskContext;

/**
 * Created by Kun Yang on 16/1/23.
 */
public interface TaskCatcherRun {

    void catchThrow(Throwable cause, TaskContext context);

}
