package com.tny.game.event;

/**
 * Created by Kun Yang on 16/2/4.
 */
@FunctionalInterface
public interface VoidEventInvoker<L, S> {

    void invoke(L listener, S source);

}
