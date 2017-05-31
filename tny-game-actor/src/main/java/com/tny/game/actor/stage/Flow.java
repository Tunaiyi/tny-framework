package com.tny.game.actor.stage;

/**
 * 流程
 * Created by Kun Yang on 2017/5/30.
 */
public interface Flow extends Runnable {

    boolean isDone();

    boolean isFailed();

    boolean isSuccess();

    Throwable getCause();

    void cancel();

    <T> Stage<T> first(Object name);

}
