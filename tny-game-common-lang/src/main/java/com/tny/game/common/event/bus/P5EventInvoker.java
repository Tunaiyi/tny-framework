package com.tny.game.common.event.bus;

/**
 * Created by Kun Yang on 16/2/4.
 */
@FunctionalInterface
public interface P5EventInvoker<L, S, A1, A2, A3, A4, A5> {

    void invoke(L listener, S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5);

}
