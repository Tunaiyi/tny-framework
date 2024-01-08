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
public class A5BindEvent<L, S, A1, A2, A3, A4, A5> extends ArgsBindEvent<L,
        Args5EventInvoker<L, S, A1, A2, A3, A4, A5>,
        Args5EventDelegate<S, A1, A2, A3, A4, A5>,
        S, A1, A2, A3, A4, A5,
        A5BindEvent<L, S, A1, A2, A3, A4, A5>> {

    private A5BindEvent(A5BindEvent<L, S, A1, A2, A3, A4, A5> parent) {
        super(parent);
    }

    public A5BindEvent(Class<L> bindClass, Args5EventInvoker<L, S, A1, A2, A3, A4, A5> invoker, boolean global) {
        super(bindClass, invoker, global);
    }

    public A5BindEvent(Class<L> bindWith, Args5EventInvoker<L, S, A1, A2, A3, A4, A5> invoker,
            Supplier<List<Args5EventDelegate<S, A1, A2, A3, A4, A5>>> factory, boolean global) {
        super(bindWith, invoker, factory, global);
    }

    @Override
    public void addListener(L handler) {
        this.add(new ThisBindHandler(handler));
    }

    @Override
    public void removeListener(L handler) {
        this.remove(new ThisBindHandler(handler));
    }

    public void notify(S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        doNotify(source, a1, a2, a3, a4, a5);
    }

    @Override
    public void doParentNotify(A5BindEvent<L, S, A1, A2, A3, A4, A5> parent, S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        parent.notify(source, a1, a2, a3, a4, a5);
    }

    @Override
    public void doListenerNotify(L listener, S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        this.invoker.invoke(listener, source, a1, a2, a3, a4, a5);
    }

    @Override
    public void doDelegateNotify(Args5EventDelegate<S, A1, A2, A3, A4, A5> delegate, S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        delegate.invoke(source, a1, a2, a3, a4, a5);
    }

    @Override
    public A5BindEvent<L, S, A1, A2, A3, A4, A5> forkChild() {
        return new A5BindEvent<>(this);
    }

    class ThisBindHandler extends BindHandler<L> implements Args5EventDelegate<S, A1, A2, A3, A4, A5> {

        public ThisBindHandler(L handler) {
            super(handler);
        }

        @Override
        public void invoke(S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
            A5BindEvent.this.invoker.invoke(this.handler, source, a1, a2, a3, a4, a5);
        }

    }

}