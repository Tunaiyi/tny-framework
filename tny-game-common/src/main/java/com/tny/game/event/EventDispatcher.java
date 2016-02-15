package com.tny.game.event;

/**
 * Created by Kun Yang on 16/2/4.
 */
public interface EventDispatcher {

    <L, S> void dispatch(BindVoidEventBus<L, S> type, S source);

    <L, S, A> void dispatch(BindP1EventBus<L, S, Integer> type, S source, A arg);

    <L, S, A1, A2> void dispatch(BindP2EventBus<L, S, A1, A2> type, S source, A1 arg1, A2 arg2);

    <L, S, A1, A2, A3> void dispatch(BindP3EventBus<L, S, A1, A2, A3> type, S source, A1 arg1, A2 arg2, A2 arg3);

    <L, S, A1, A2, A3, A4> void dispatch(BindP4EventBus<L, S, A1, A2, A3, A4> type, S source, A1 arg1, A2 arg2, A2 arg3);

    <L, S, A1, A2, A3, A4, A5> void dispatch(BindP5EventBus<L, S, A1, A2, A3, A4, A5> type, S source, A1 arg1, A2 arg2, A2 arg3);

}
