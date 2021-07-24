package com.tny.game.common.event.bus;

import com.tny.game.common.event.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Kun Yang on 16/2/4.
 */
public final class EventBuses {

    private EventBuses() {
    }

    public static <L, E extends Event<?>> BindComEventBus<L, E> ofEvent(Class<L> clazz, ComEventInvoker<L, E> fn, List<ComEventDelegate<E>> listeners,
            boolean global) {
        return new BindComEventBus<>(clazz, fn, listeners, global);
    }

    public static <L, E extends Event<?>> BindComEventBus<L, E> ofEvent(Class<L> clazz, ComEventInvoker<L, E> fn,
            List<ComEventDelegate<E>> listeners) {
        return new BindComEventBus<>(clazz, fn, listeners, true);
    }

    public static <L, E extends Event<?>> BindComEventBus<L, E> ofEvent(Class<L> clazz, ComEventInvoker<L, E> fn, boolean global) {
        return new BindComEventBus<>(clazz, fn, null, global);
    }

    public static <L, E extends Event<?>> BindComEventBus<L, E> ofEvent(Class<L> clazz, ComEventInvoker<L, E> fn) {
        return new BindComEventBus<>(clazz, fn, null, true);
    }

    public static <L, S> BindVoidEventBus<L, S> of(Class<L> clazz, VoidEventInvoker<L, S> fn, List<VoidEventDelegate<S>> listeners, boolean global) {
        return new BindVoidEventBus<>(clazz, fn, listeners, global);
    }

    public static <L, S> BindVoidEventBus<L, S> of(Class<L> clazz, VoidEventInvoker<L, S> fn, List<VoidEventDelegate<S>> listeners) {
        return new BindVoidEventBus<>(clazz, fn, listeners, true);
    }

    public static <L, S> BindVoidEventBus<L, S> of(Class<L> clazz, VoidEventInvoker<L, S> fn, boolean global) {
        return new BindVoidEventBus<>(clazz, fn, null, global);
    }

    public static <L, S> BindVoidEventBus<L, S> of(Class<L> clazz, VoidEventInvoker<L, S> fn) {
        return new BindVoidEventBus<>(clazz, fn, null, true);
    }

    public static <L, S, A> BindP1EventBus<L, S, A> of(Class<L> clazz, P1EventInvoker<L, S, A> fn, List<P1EventDelegate<S, A>> listeners,
            boolean global) {
        return new BindP1EventBus<>(clazz, fn, listeners, global);
    }

    public static <L, S, A> BindP1EventBus<L, S, A> of(Class<L> clazz, P1EventInvoker<L, S, A> fn, List<P1EventDelegate<S, A>> listeners) {
        return new BindP1EventBus<>(clazz, fn, listeners, true);
    }

    public static <L, S, A> BindP1EventBus<L, S, A> of(Class<L> clazz, P1EventInvoker<L, S, A> fn, boolean global) {
        return new BindP1EventBus<>(clazz, fn, null, global);
    }

    public static <L, S, A> BindP1EventBus<L, S, A> of(Class<L> clazz, P1EventInvoker<L, S, A> fn) {
        return new BindP1EventBus<>(clazz, fn, null, true);
    }

    public static <L, S, A1, A2> BindP2EventBus<L, S, A1, A2> of(Class<L> clazz, P2EventInvoker<L, S, A1, A2> fn,
            List<P2EventDelegate<S, A1, A2>> listeners, boolean global) {
        return new BindP2EventBus<>(clazz, fn, listeners, global);
    }

    public static <L, S, A1, A2> BindP2EventBus<L, S, A1, A2> of(Class<L> clazz, P2EventInvoker<L, S, A1, A2> fn,
            List<P2EventDelegate<S, A1, A2>> listeners) {
        return new BindP2EventBus<>(clazz, fn, listeners, true);
    }

    public static <L, S, A1, A2> BindP2EventBus<L, S, A1, A2> of(Class<L> clazz, P2EventInvoker<L, S, A1, A2> fn, boolean global) {
        return new BindP2EventBus<>(clazz, fn, null, global);
    }

    public static <L, S, A1, A2> BindP2EventBus<L, S, A1, A2> of(Class<L> clazz, P2EventInvoker<L, S, A1, A2> fn) {
        return new BindP2EventBus<>(clazz, fn, null, true);
    }

    public static <L, S, A1, A2, A3> BindP3EventBus<L, S, A1, A2, A3> of(Class<L> clazz, P3EventInvoker<L, S, A1, A2, A3> fn,
            List<P3EventDelegate<S, A1, A2, A3>> listeners, boolean global) {
        return new BindP3EventBus<>(clazz, fn, listeners, global);
    }

    public static <L, S, A1, A2, A3> BindP3EventBus<L, S, A1, A2, A3> of(Class<L> clazz, P3EventInvoker<L, S, A1, A2, A3> fn,
            List<P3EventDelegate<S, A1, A2, A3>> listeners) {
        return new BindP3EventBus<>(clazz, fn, listeners, true);
    }

    public static <L, S, A1, A2, A3> BindP3EventBus<L, S, A1, A2, A3> of(Class<L> clazz, P3EventInvoker<L, S, A1, A2, A3> fn, boolean global) {
        return new BindP3EventBus<>(clazz, fn, null, global);
    }

    public static <L, S, A1, A2, A3> BindP3EventBus<L, S, A1, A2, A3> of(Class<L> clazz, P3EventInvoker<L, S, A1, A2, A3> fn) {
        return new BindP3EventBus<>(clazz, fn, null, true);
    }

    public static <L, S, A1, A2, A3, A4> BindP4EventBus<L, S, A1, A2, A3, A4> of(Class<L> clazz, P4EventInvoker<L, S, A1, A2, A3, A4> fn,
            List<P4EventDelegate<S, A1, A2, A3, A4>> listeners, boolean global) {
        return new BindP4EventBus<>(clazz, fn, listeners, global);
    }

    public static <L, S, A1, A2, A3, A4> BindP4EventBus<L, S, A1, A2, A3, A4> of(Class<L> clazz, P4EventInvoker<L, S, A1, A2, A3, A4> fn,
            List<P4EventDelegate<S, A1, A2, A3, A4>> listeners) {
        return new BindP4EventBus<>(clazz, fn, listeners, true);
    }

    public static <L, S, A1, A2, A3, A4> BindP4EventBus<L, S, A1, A2, A3, A4> of(Class<L> clazz, P4EventInvoker<L, S, A1, A2, A3, A4> fn,
            boolean global) {
        return new BindP4EventBus<>(clazz, fn, null, global);
    }

    public static <L, S, A1, A2, A3, A4> BindP4EventBus<L, S, A1, A2, A3, A4> of(Class<L> clazz, P4EventInvoker<L, S, A1, A2, A3, A4> fn) {
        return new BindP4EventBus<>(clazz, fn, null, true);
    }

    public static <L, S, A1, A2, A3, A4, A5> BindP5EventBus<L, S, A1, A2, A3, A4, A5> of(Class<L> clazz, P5EventInvoker<L, S, A1, A2, A3, A4, A5> fn,
            List<P5EventDelegate<S, A1, A2, A3, A4, A5>> listeners, boolean global) {
        return new BindP5EventBus<>(clazz, fn, listeners, global);
    }

    public static <L, S, A1, A2, A3, A4, A5> BindP5EventBus<L, S, A1, A2, A3, A4, A5> of(Class<L> clazz, P5EventInvoker<L, S, A1, A2, A3, A4, A5> fn,
            List<P5EventDelegate<S, A1, A2, A3, A4, A5>> listeners) {
        return new BindP5EventBus<>(clazz, fn, listeners, true);
    }

    public static <L, S, A1, A2, A3, A4, A5> BindP5EventBus<L, S, A1, A2, A3, A4, A5> of(Class<L> clazz, P5EventInvoker<L, S, A1, A2, A3, A4, A5> fn,
            boolean global) {
        return new BindP5EventBus<>(clazz, fn, null, global);
    }

    public static <L, S, A1, A2, A3, A4, A5> BindP5EventBus<L, S, A1, A2, A3, A4, A5> of(Class<L> clazz,
            P5EventInvoker<L, S, A1, A2, A3, A4, A5> fn) {
        return new BindP5EventBus<>(clazz, fn, null, true);
    }

    public static <E extends Event<?>> ComEventBus<E> newComEvent() {
        return new ComEventBus<>(new CopyOnWriteArrayList<>());
    }

    public static <S> VoidEventBus<S> newVoidEvent() {
        return new VoidEventBus<>(new CopyOnWriteArrayList<>());
    }

    public static <S, A> P1EventBus<S, A> newP1Event() {
        return new P1EventBus<>(new CopyOnWriteArrayList<>());
    }

    public static <S, A1, A2> P2EventBus<S, A1, A2> newP2Event() {
        return new P2EventBus<>(new CopyOnWriteArrayList<>());
    }

    public static <S, A1, A2, A3> P3EventBus<S, A1, A2, A3> newP3Event() {
        return new P3EventBus<>(new CopyOnWriteArrayList<>());
    }

    public static <S, A1, A2, A3, A4> P4EventBus<S, A1, A2, A3, A4> newP4Event() {
        return new P4EventBus<>(new CopyOnWriteArrayList<>());
    }

    public static <S, A1, A2, A3, A4, A5> P5EventBus<S, A1, A2, A3, A4, A5> newP5Event() {
        return new P5EventBus<>(new CopyOnWriteArrayList<>());
    }

    public static <E extends Event<?>> ComEventBus<E> newComEvent(List<ComEventDelegate<E>> listeners) {
        return new ComEventBus<>(listeners);
    }

    public static <S> VoidEventBus<S> newVoidEvent(List<VoidEventDelegate<S>> listeners) {
        return new VoidEventBus<>(listeners);
    }

    public static <S, A> P1EventBus<S, A> newP1Event(List<P1EventDelegate<S, A>> listeners) {
        return new P1EventBus<>(listeners);
    }

    public static <S, A1, A2> P2EventBus<S, A1, A2> newP2Event(List<P2EventDelegate<S, A1, A2>> listeners) {
        return new P2EventBus<>(listeners);
    }

    public static <S, A1, A2, A3> P3EventBus<S, A1, A2, A3> newP3Event(List<P3EventDelegate<S, A1, A2, A3>> listeners) {
        return new P3EventBus<>(listeners);
    }

    public static <S, A1, A2, A3, A4> P4EventBus<S, A1, A2, A3, A4> newP4Event(List<P4EventDelegate<S, A1, A2, A3, A4>> listeners) {
        return new P4EventBus<>(listeners);
    }

    public static <S, A1, A2, A3, A4, A5> P5EventBus<S, A1, A2, A3, A4, A5> newP5Event(List<P5EventDelegate<S, A1, A2, A3, A4, A5>> listeners) {
        return new P5EventBus<>(listeners);
    }

}

