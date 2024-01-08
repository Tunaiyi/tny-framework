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
public class A3BindEvent<L, S, A1, A2, A3> extends ArgsBindEvent<L,
        Args3EventInvoker<L, S, A1, A2, A3>,
        Args3EventDelegate<S, A1, A2, A3>,
        S, A1, A2, A3, Void, Void,
        A3BindEvent<L, S, A1, A2, A3>> {

    private A3BindEvent(A3BindEvent<L, S, A1, A2, A3> parent) {
        super(parent);
    }

    public A3BindEvent(Class<L> bindClass, Args3EventInvoker<L, S, A1, A2, A3> invoker, boolean global) {
        super(bindClass, invoker, global);
    }

    public A3BindEvent(Class<L> bindWith, Args3EventInvoker<L, S, A1, A2, A3> invoker,
            Supplier<List<Args3EventDelegate<S, A1, A2, A3>>> factory, boolean global) {
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

    public void notify(S source, A1 a1, A2 a2, A3 a3) {
        doNotify(source, a1, a2, a3, null, null);
    }

    @Override
    public void doParentNotify(A3BindEvent<L, S, A1, A2, A3> parent, S source, A1 a1, A2 a2, A3 a3, Void unused, Void unused2) {
        parent.notify(source, a1, a2, a3);
    }

    @Override
    public void doListenerNotify(L listener, S source, A1 a1, A2 a2, A3 a3, Void unused, Void unused2) {
        this.invoker.invoke(listener, source, a1, a2, a3);
    }

    @Override
    public void doDelegateNotify(Args3EventDelegate<S, A1, A2, A3> delegate, S source, A1 a1, A2 a2, A3 a3, Void unused, Void unused2) {
        delegate.invoke(source, a1, a2, a3);
    }

    @Override
    public A3BindEvent<L, S, A1, A2, A3> forkChild() {
        return new A3BindEvent<>(this);
    }

    class ThisBindHandler extends BindHandler<L> implements Args3EventDelegate<S, A1, A2, A3> {

        public ThisBindHandler(L handler) {
            super(handler);
        }

        @Override
        public void invoke(S source, A1 a1, A2 a2, A3 a3) {
            A3BindEvent.this.invoker.invoke(this.handler, source, a1, a2, a3);
        }

    }

}