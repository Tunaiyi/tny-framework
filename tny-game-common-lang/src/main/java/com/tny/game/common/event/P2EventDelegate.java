package com.tny.game.common.event;

/**
 * Created by Kun Yang on 16/2/4.
 */
@FunctionalInterface
public interface P2EventDelegate<S, A1, A2> {

    void invoke(S source, A1 a1, A2 a2);

}
