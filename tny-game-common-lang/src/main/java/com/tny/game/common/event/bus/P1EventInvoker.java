package com.tny.game.common.event.bus;

/**
 * Created by Kun Yang on 16/2/4.
 */
@FunctionalInterface
public interface P1EventInvoker<L, S, A> {

    void invoke(L listener, S source, A a);

}
