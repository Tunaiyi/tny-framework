package com.tny.game.common.event.trigger;

import com.tny.game.common.event.*;
import com.tny.game.common.event.bus.*;

import java.util.function.BiConsumer;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/19 5:23 下午
 */
public interface EventTrigger<L, S> extends EventListenable<L> {

    void trigger(BiConsumer<L, S> invoker, S source);

    <E extends Event<S>> void trigger(ComEventInvoker<L, E> invoker, E event);

    <A> void trigger(P1EventInvoker<L, S, A> invoker, S source, A a);

    <A1, A2> void trigger(P2EventInvoker<L, S, A1, A2> invoker, S source, A1 a1, A2 a2);

    <A1, A2, A3> void trigger(P3EventInvoker<L, S, A1, A2, A3> invoker, S source, A1 a1, A2 a2, A3 a3);

    <A1, A2, A3, A4> void trigger(P4EventInvoker<L, S, A1, A2, A3, A4> invoker, S source, A1 a1, A2 a2, A3 a3, A4 a4);

    <A1, A2, A3, A4, A5> void trigger(P5EventInvoker<L, S, A1, A2, A3, A4, A5> invoker, S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5);

}
