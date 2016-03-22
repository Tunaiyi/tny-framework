package drama.stage.invok;

import drama.stage.TaskContext;

/**
 * Created by Kun Yang on 16/1/22.
 */
@FunctionalInterface
public interface TaskSupplier<R> {

    R get(TaskContext context);

}
