package com.tny.game.event;

/**
 * Created by Kun Yang on 16/2/4.
 */
@FunctionalInterface
public interface P1EventDelegate<S, A> {

    void invoke(S source, A a);

}
