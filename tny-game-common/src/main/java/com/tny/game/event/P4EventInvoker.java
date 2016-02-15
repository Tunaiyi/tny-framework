package com.tny.game.event;

/**
 * Created by Kun Yang on 16/2/4.
 */
@FunctionalInterface
public interface P4EventInvoker<L, S, A1, A2, A3, A4> {

    void invoke(L listener, S source, A1 a1, A2 a2, A3 a3, A4 a4);

}
