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
public class A2BindEvent<L, S, A1, A2> extends ArgsBindEvent<L,
        Args2EventInvoker<L, S, A1, A2>,
        Args2EventDelegate<S, A1, A2>,
        S, A1, A2, Void, Void, Void,
        A2BindEvent<L, S, A1, A2>> {

    private A2BindEvent(A2BindEvent<L, S, A1, A2> parent) {
        super(parent);
    }

    public A2BindEvent(Class<L> bindClass, Args2EventInvoker<L, S, A1, A2> invoker, boolean global) {
        super(bindClass, invoker, global);
    }

    public A2BindEvent(Class<L> bindWith, Args2EventInvoker<L, S, A1, A2> invoker,
            Supplier<Collection<Args2EventDelegate<S, A1, A2>>> factory, boolean global) {
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

    public void notify(S source, A1 a1, A2 a2) {
        doNotify(source, a1, a2, null, null, null);
    }

    @Override
    public void doParentNotify(A2BindEvent<L, S, A1, A2> parent, S source, A1 a1, A2 a2, Void unused, Void unused2, Void unused3) {
        parent.notify(source, a1, a2);
    }

    @Override
    public void doListenerNotify(L listener, S source, A1 a1, A2 a2, Void unused, Void unused2, Void unused3) {
        this.invoker.invoke(listener, source, a1, a2);
    }

    @Override
    public void doDelegateNotify(Args2EventDelegate<S, A1, A2> delegate, S source, A1 a1, A2 a2, Void unused, Void unused2, Void unused3) {
        delegate.invoke(source, a1, a2);
    }

    @Override
    public A2BindEvent<L, S, A1, A2> forkChild() {
        return new A2BindEvent<>(this);
    }

    class ThisBindHandler extends BindHandler<L> implements Args2EventDelegate<S, A1, A2> {

        public ThisBindHandler(L handler) {
            super(handler);
        }

        @Override
        public void invoke(S source, A1 a1, A2 a2) {
            A2BindEvent.this.invoker.invoke(this.handler, source, a1, a2);
        }

    }

}
