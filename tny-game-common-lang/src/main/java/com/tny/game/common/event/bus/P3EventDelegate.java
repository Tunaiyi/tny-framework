package com.tny.game.common.event.bus;

/**
 * Created by Kun Yang on 16/2/4.
 */
@FunctionalInterface
public interface P3EventDelegate<S, A1, A2, A3> {

    void invoke(S source, A1 a1, A2 a2, A3 a3);

}
