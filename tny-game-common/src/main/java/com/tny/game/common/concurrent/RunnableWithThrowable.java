package com.tny.game.common.concurrent;

/**
 * Created by Kun Yang on 2016/12/16.
 */
@FunctionalInterface
public interface RunnableWithThrowable {

    void run() throws Throwable;

}
