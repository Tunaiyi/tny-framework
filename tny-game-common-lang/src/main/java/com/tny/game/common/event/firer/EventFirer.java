package com.tny.game.common.event.firer;

import com.tny.game.common.event.*;
import com.tny.game.common.event.bus.*;

import java.util.function.BiConsumer;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/19 5:23 下午
 */
public interface EventFirer<L, S> extends EventSource<L> {

    void fire(BiConsumer<L, S> invoker, S source);

    <E extends Event<S>> void fire(ComEventInvoker<L, E> invoker, E event);

    <A> void fire(P1EventInvoker<L, S, A> invoker, S source, A a);

    <A1, A2> void fire(P2EventInvoker<L, S, A1, A2> invoker, S source, A1 a1, A2 a2);

    <A1, A2, A3> void fire(P3EventInvoker<L, S, A1, A2, A3> invoker, S source, A1 a1, A2 a2, A3 a3);

    <A1, A2, A3, A4> void fire(P4EventInvoker<L, S, A1, A2, A3, A4> invoker, S source, A1 a1, A2 a2, A3 a3, A4 a4);

    <A1, A2, A3, A4, A5> void fire(P5EventInvoker<L, S, A1, A2, A3, A4, A5> invoker, S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5);

}
