package com.tny.game.actor.task.invok;

/**
 * Created by Kun Yang on 16/1/23.
 */
public interface CatcherSupplier<R> {

    R catchThrow(Throwable cause);

}
