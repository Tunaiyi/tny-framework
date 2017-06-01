package com.tny.game.actor.stage;

import java.util.concurrent.Executor;

/**
 * 流程
 * Created by Kun Yang on 2017/5/30.
 */
public interface Flow extends Runnable {

    boolean isDone();

    boolean isFailed();

    boolean isSuccess();

    Throwable getCause();

    Object getResult();

    void cancel();

    <T> Stage<T> find(Object name);

    Flow start();

    Flow start(Executor executor);

}
