package com.tny.game.common.event;

/**
 * Created by Kun Yang on 16/2/4.
 */
@FunctionalInterface
public interface P5EventDelegate<S, A1, A2, A3, A4, A5> {

    void invoke(S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5);

}
