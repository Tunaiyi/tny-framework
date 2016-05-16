package com.tny.game.actor.stage;

import com.tny.game.common.utils.Do;
import com.tny.game.common.utils.Done;

@SuppressWarnings("unchecked")
public class StageUtils {

    @SuppressWarnings("unchecked")
    protected static <T extends CommonTaskStage> T as(Object o) {
        return (T) o;
    }

    public static void run(TaskStage stage) {
        as(stage).start();
    }

    public static void cancel(TaskStage stage) {
        as(stage).cancel();
    }

    public static <T> Done<T> getResult(TaskStage stage) {
        if (!stage.isDone())
            return Do.fail();
        if (stage instanceof TypeTaskStage) {
            TypeTaskStage<T> typeStage = (TypeTaskStage<T>) stage;
            return Do.succNullable(typeStage.getResult());
        } else if (stage instanceof VoidTaskStage) {
            return Do.succNullable(null);
        }
        return Do.fail();
    }

    public static <T> Done<T> getCause(TaskStage stage) {
        if (!stage.isDone())
            return Do.fail();
        if (stage instanceof TypeTaskStage) {
            TypeTaskStage<T> typeStage = (TypeTaskStage<T>) stage;
            return Do.succNullable(typeStage.getResult());
        } else if (stage instanceof VoidTaskStage) {
            return Do.succNullable(null);
        }
        return Do.fail();
    }

}
