package com.tny.game.common.event.bus;

/**
 * Created by Kun Yang on 16/2/4.
 */
@FunctionalInterface
public interface VoidEventDelegate<S> {

    void invoke(S source);

}