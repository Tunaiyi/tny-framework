package com.tny.game.actor.task;

/**
 * Created by Kun Yang on 16/4/23.
 */
public class StageUtils {

    @SuppressWarnings("unchecked")
    protected static <T extends CommonTaskStage> T as(Object o) {
        return (T) o;
    }

    public static void run(VoidTaskStage stage) {
        as(stage).start();
    }

}
