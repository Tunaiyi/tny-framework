package com.tny.game.actor.stage;

/**
 * Created by Kun Yang on 16/4/23.
 */
public interface TaskStage {

    boolean isDone();

    boolean isFailed();

    boolean isSuccess();

    Throwable getCause();

}
