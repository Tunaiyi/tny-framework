package com.tny.game.common.event.bus;

/**
 * Created by Kun Yang on 16/2/4.
 */
@FunctionalInterface
public interface P2EventInvoker<L, S, A1, A2> {

    void invoke(L listener, S source, A1 a1, A2 a2);

}
