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

package com.tny.game.common.event;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class A4BindEvent<L, S, A1, A2, A3, A4> extends ArgsBindEvent<L,
        Args4EventInvoker<L, S, A1, A2, A3, A4>,
        Args4EventDelegate<S, A1, A2, A3, A4>,
        S, A1, A2, A3, A4, Void,
        A4BindEvent<L, S, A1, A2, A3, A4>> {

    private A4BindEvent(A4BindEvent<L, S, A1, A2, A3, A4> parent) {
        super(parent);
    }

    public A4BindEvent(Class<L> bindClass, Args4EventInvoker<L, S, A1, A2, A3, A4> invoker, boolean global) {
        super(bindClass, invoker, global);
    }

    public A4BindEvent(Class<L> bindWith, Args4EventInvoker<L, S, A1, A2, A3, A4> invoker,
            Supplier<Collection<Args4EventDelegate<S, A1, A2, A3, A4>>> factory, boolean global) {
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

    public void notify(S source, A1 a1, A2 a2, A3 a3, A4 a4) {
        doNotify(source, a1, a2, a3, a4, null);
    }

    @Override
    public void doParentNotify(A4BindEvent<L, S, A1, A2, A3, A4> parent, S source, A1 a1, A2 a2, A3 a3, A4 a4, Void unused) {
        parent.notify(source, a1, a2, a3, a4);
    }

    @Override
    public void doListenerNotify(L listener, S source, A1 a1, A2 a2, A3 a3, A4 a4, Void unused) {
        this.invoker.invoke(listener, source, a1, a2, a3, a4);
    }

    @Override
    public void doDelegateNotify(Args4EventDelegate<S, A1, A2, A3, A4> delegate, S source, A1 a1, A2 a2, A3 a3, A4 a4, Void unused) {
        delegate.invoke(source, a1, a2, a3, a4);
    }

    @Override
    public A4BindEvent<L, S, A1, A2, A3, A4> forkChild() {
        return new A4BindEvent<>(this);
    }

    class ThisBindHandler extends BindHandler<L> implements Args4EventDelegate<S, A1, A2, A3, A4> {

        public ThisBindHandler(L handler) {
            super(handler);
        }

        @Override
        public void invoke(S source, A1 a1, A2 a2, A3 a3, A4 a4) {
            A4BindEvent.this.invoker.invoke(this.handler, source, a1, a2, a3, a4);
        }

    }

}