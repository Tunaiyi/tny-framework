package com.tny.game.actor.stage;

import com.tny.game.common.utils.DoneUtils;
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
            return DoneUtils.fail();
        if (stage instanceof TypeTaskStage) {
            TypeTaskStage<T> typeStage = (TypeTaskStage<T>) stage;
            return DoneUtils.succNullable(typeStage.getResult());
        } else if (stage instanceof VoidTaskStage) {
            return DoneUtils.succNullable(null);
        }
        return DoneUtils.fail();
    }

    public static <T> Done<T> getCause(TaskStage stage) {
        if (!stage.isDone())
            return DoneUtils.fail();
        if (stage instanceof TypeTaskStage) {
            TypeTaskStage<T> typeStage = (TypeTaskStage<T>) stage;
            return DoneUtils.succNullable(typeStage.getResult());
        } else if (stage instanceof VoidTaskStage) {
            return DoneUtils.succNullable(null);
        }
        return DoneUtils.fail();
    }

}
