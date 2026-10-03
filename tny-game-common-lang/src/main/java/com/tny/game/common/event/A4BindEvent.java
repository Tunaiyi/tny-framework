/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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