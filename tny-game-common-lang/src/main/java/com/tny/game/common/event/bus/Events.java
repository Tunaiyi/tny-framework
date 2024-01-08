/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.event.bus;

import java.util.List;
import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/2/4.
 */
public final class Events {

    private Events() {
    }

    public static <L, E extends EventNotice<?>> GeneralBindEvent<L, E> ofGeneral(Class<L> clazz, GeneralEventInvoker<L, E> fn,
            Supplier<List<GeneralEventDelegate<E>>> factory, boolean global) {
        return new GeneralBindEvent<>(clazz, fn, factory, global);
    }

    public static <L, E extends EventNotice<?>> GeneralBindEvent<L, E> ofGeneral(Class<L> clazz, GeneralEventInvoker<L, E> fn,
            Supplier<List<GeneralEventDelegate<E>>> listeners) {
        return new GeneralBindEvent<>(clazz, fn, listeners, true);
    }

    public static <L, E extends EventNotice<?>> GeneralBindEvent<L, E> ofGeneral(Class<L> clazz, GeneralEventInvoker<L, E> fn, boolean global) {
        return new GeneralBindEvent<>(clazz, fn, null, global);
    }

    public static <L, E extends EventNotice<?>> GeneralBindEvent<L, E> ofGeneral(Class<L> clazz, GeneralEventInvoker<L, E> fn) {
        return new GeneralBindEvent<>(clazz, fn, null, true);
    }

    public static <L, S> VoidBindEvent<L, S> ofEvent(Class<L> clazz, VoidEventInvoker<L, S> fn, Supplier<List<VoidEventDelegate<S>>> factory,
            boolean global) {
        return new VoidBindEvent<>(clazz, fn, factory, global);
    }

    public static <L, S> VoidBindEvent<L, S> ofEvent(Class<L> clazz, VoidEventInvoker<L, S> fn, Supplier<List<VoidEventDelegate<S>>> factory) {
        return new VoidBindEvent<>(clazz, fn, factory, true);
    }

    public static <L, S> VoidBindEvent<L, S> ofEvent(Class<L> clazz, VoidEventInvoker<L, S> fn, boolean global) {
        return new VoidBindEvent<>(clazz, fn, null, global);
    }

    public static <L, S> VoidBindEvent<L, S> ofEvent(Class<L> clazz, VoidEventInvoker<L, S> fn) {
        return new VoidBindEvent<>(clazz, fn, null, true);
    }

    public static <L, S, A> A1BindEvent<L, S, A> ofEvent(Class<L> clazz, Arg1EventInvoker<L, S, A> fn,
            Supplier<List<Arg1EventDelegate<S, A>>> factory, boolean global) {
        return new A1BindEvent<>(clazz, fn, factory, global);
    }

    public static <L, S, A> A1BindEvent<L, S, A> ofEvent(Class<L> clazz, Arg1EventInvoker<L, S, A> fn,
            Supplier<List<Arg1EventDelegate<S, A>>> factory) {
        return new A1BindEvent<>(clazz, fn, factory, true);
    }

    public static <L, S, A> A1BindEvent<L, S, A> ofEvent(Class<L> clazz, Arg1EventInvoker<L, S, A> fn, boolean global) {
        return new A1BindEvent<>(clazz, fn, null, global);
    }

    public static <L, S, A> A1BindEvent<L, S, A> ofEvent(Class<L> clazz, Arg1EventInvoker<L, S, A> fn) {
        return new A1BindEvent<>(clazz, fn, null, true);
    }

    public static <L, S, A1, A2> A2BindEvent<L, S, A1, A2> ofEvent(Class<L> clazz, Args2EventInvoker<L, S, A1, A2> fn,
            Supplier<List<Args2EventDelegate<S, A1, A2>>> factory, boolean global) {
        return new A2BindEvent<>(clazz, fn, factory, global);
    }

    public static <L, S, A1, A2> A2BindEvent<L, S, A1, A2> ofEvent(Class<L> clazz, Args2EventInvoker<L, S, A1, A2> fn,
            Supplier<List<Args2EventDelegate<S, A1, A2>>> factory) {
        return new A2BindEvent<>(clazz, fn, factory, true);
    }

    public static <L, S, A1, A2> A2BindEvent<L, S, A1, A2> ofEvent(Class<L> clazz, Args2EventInvoker<L, S, A1, A2> fn, boolean global) {
        return new A2BindEvent<>(clazz, fn, null, global);
    }

    public static <L, S, A1, A2> A2BindEvent<L, S, A1, A2> ofEvent(Class<L> clazz, Args2EventInvoker<L, S, A1, A2> fn) {
        return new A2BindEvent<>(clazz, fn, null, true);
    }

    public static <L, S, A1, A2, A3> A3BindEvent<L, S, A1, A2, A3> ofEvent(Class<L> clazz, Args3EventInvoker<L, S, A1, A2, A3> fn,
            Supplier<List<Args3EventDelegate<S, A1, A2, A3>>> factory, boolean global) {
        return new A3BindEvent<>(clazz, fn, factory, global);
    }

    public static <L, S, A1, A2, A3> A3BindEvent<L, S, A1, A2, A3> ofEvent(Class<L> clazz, Args3EventInvoker<L, S, A1, A2, A3> fn,
            Supplier<List<Args3EventDelegate<S, A1, A2, A3>>> factory) {
        return new A3BindEvent<>(clazz, fn, factory, true);
    }

    public static <L, S, A1, A2, A3> A3BindEvent<L, S, A1, A2, A3> ofEvent(Class<L> clazz, Args3EventInvoker<L, S, A1, A2, A3> fn, boolean global) {
        return new A3BindEvent<>(clazz, fn, null, global);
    }

    public static <L, S, A1, A2, A3> A3BindEvent<L, S, A1, A2, A3> ofEvent(Class<L> clazz, Args3EventInvoker<L, S, A1, A2, A3> fn) {
        return new A3BindEvent<>(clazz, fn, null, true);
    }

    public static <L, S, A1, A2, A3, A4> A4BindEvent<L, S, A1, A2, A3, A4> ofEvent(Class<L> clazz, Args4EventInvoker<L, S, A1, A2, A3, A4> fn,
            Supplier<List<Args4EventDelegate<S, A1, A2, A3, A4>>> factory, boolean global) {
        return new A4BindEvent<>(clazz, fn, factory, global);
    }

    public static <L, S, A1, A2, A3, A4> A4BindEvent<L, S, A1, A2, A3, A4> ofEvent(Class<L> clazz, Args4EventInvoker<L, S, A1, A2, A3, A4> fn,
            Supplier<List<Args4EventDelegate<S, A1, A2, A3, A4>>> factory) {
        return new A4BindEvent<>(clazz, fn, factory, true);
    }

    public static <L, S, A1, A2, A3, A4> A4BindEvent<L, S, A1, A2, A3, A4> ofEvent(Class<L> clazz, Args4EventInvoker<L, S, A1, A2, A3, A4> fn,
            boolean global) {
        return new A4BindEvent<>(clazz, fn, null, global);
    }

    public static <L, S, A1, A2, A3, A4> A4BindEvent<L, S, A1, A2, A3, A4> ofEvent(Class<L> clazz, Args4EventInvoker<L, S, A1, A2, A3, A4> fn) {
        return new A4BindEvent<>(clazz, fn, null, true);
    }

    public static <L, S, A1, A2, A3, A4, A5> A5BindEvent<L, S, A1, A2, A3, A4, A5> ofEvent(Class<L> clazz,
            Args5EventInvoker<L, S, A1, A2, A3, A4, A5> fn, Supplier<List<Args5EventDelegate<S, A1, A2, A3, A4, A5>>> factory, boolean global) {
        return new A5BindEvent<>(clazz, fn, factory, global);
    }

    public static <L, S, A1, A2, A3, A4, A5> A5BindEvent<L, S, A1, A2, A3, A4, A5> ofEvent(Class<L> clazz,
            Args5EventInvoker<L, S, A1, A2, A3, A4, A5> fn, Supplier<List<Args5EventDelegate<S, A1, A2, A3, A4, A5>>> factory) {
        return new A5BindEvent<>(clazz, fn, factory, true);
    }

    public static <L, S, A1, A2, A3, A4, A5> A5BindEvent<L, S, A1, A2, A3, A4, A5> ofEvent(Class<L> clazz,
            Args5EventInvoker<L, S, A1, A2, A3, A4, A5> fn, boolean global) {
        return new A5BindEvent<>(clazz, fn, null, global);
    }

    public static <L, S, A1, A2, A3, A4, A5> A5BindEvent<L, S, A1, A2, A3, A4, A5> ofEvent(Class<L> clazz,
            Args5EventInvoker<L, S, A1, A2, A3, A4, A5> fn) {
        return new A5BindEvent<>(clazz, fn, null, true);
    }


    public static <E extends EventNotice<?>> GeneralEvent<E> ofGeneral() {
        return new GeneralEvent<>();
    }

    public static <S> VoidEvent<S> ofVoidEvent() {
        return new VoidEvent<>();
    }

    public static <S, A> A1Event<S, A> ofA1Event() {
        return new A1Event<>();
    }

    public static <S, A1, A2> A2Event<S, A1, A2> ofA2Event() {
        return new A2Event<>();
    }

    public static <S, A1, A2, A3> A3Event<S, A1, A2, A3> ofA3Event() {
        return new A3Event<>();
    }

    public static <S, A1, A2, A3, A4> A4Event<S, A1, A2, A3, A4> ofA4Event() {
        return new A4Event<>();
    }

    public static <S, A1, A2, A3, A4, A5> A5Event<S, A1, A2, A3, A4, A5> ofA5Event() {
        return new A5Event<>();
    }


    public static <S> VoidEvent<S> ofVoidEvent(Supplier<List<VoidEventDelegate<S>>> factory) {
        return new VoidEvent<>(factory);
    }

    public static <S, A> A1Event<S, A> ofA1Event(Supplier<List<Arg1EventDelegate<S, A>>> factory) {
        return new A1Event<>(factory);
    }

    public static <S, A1, A2> A2Event<S, A1, A2> ofA2Event(Supplier<List<Args2EventDelegate<S, A1, A2>>> factory) {
        return new A2Event<>(factory);
    }

    public static <S, A1, A2, A3> A3Event<S, A1, A2, A3> ofA3Event(Supplier<List<Args3EventDelegate<S, A1, A2, A3>>> factory) {
        return new A3Event<>(factory);
    }

    public static <S, A1, A2, A3, A4> A4Event<S, A1, A2, A3, A4> ofA4Event(Supplier<List<Args4EventDelegate<S, A1, A2, A3, A4>>> factory) {
        return new A4Event<>(factory);
    }

    public static <S, A1, A2, A3, A4, A5> A5Event<S, A1, A2, A3, A4, A5> ofA5Event(
            Supplier<List<Args5EventDelegate<S, A1, A2, A3, A4, A5>>> factory) {
        return new A5Event<>(factory);
    }

}

