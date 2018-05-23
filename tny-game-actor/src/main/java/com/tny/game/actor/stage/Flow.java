package com.tny.game.actor.stage;

import com.tny.game.common.concurrent.Waitable;

import java.util.concurrent.Executor;

/**
 * 流程
 * Created by Kun Yang on 2017/5/30.
 */
public interface Flow extends Runnable, Waitable<Object> {

    void cancel();

    <T> Stage<T> find(Object name);

    Flow start();

    Flow start(Executor executor);

}