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
public class A1BindEvent<L, S, A> extends ArgsBindEvent<L,
        Arg1EventInvoker<L, S, A>,
        Arg1EventDelegate<S, A>,
        S, A, Void, Void, Void, Void,
        A1BindEvent<L, S, A>> {

    private A1BindEvent(A1BindEvent<L, S, A> parent) {
        super(parent);
    }

    public A1BindEvent(Class<L> bindClass, Arg1EventInvoker<L, S, A> invoker, boolean global) {
        super(bindClass, invoker, global);
    }

    public A1BindEvent(Class<L> bindWith, Arg1EventInvoker<L, S, A> invoker, Supplier<Collection<Arg1EventDelegate<S, A>>> factory,
            boolean global) {
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

    public void notify(S source, A a) {
        doNotify(source, a, null, null, null, null);
    }

    @Override
    public void doParentNotify(A1BindEvent<L, S, A> parent, S source, A a, Void unused, Void unused2, Void unused3, Void unused4) {
        parent.notify(source, a);
    }

    @Override
    public void doListenerNotify(L listener, S source, A a, Void unused, Void unused2, Void unused3, Void unused4) {
        this.invoker.invoke(listener, source, a);
    }

    @Override
    public void doDelegateNotify(Arg1EventDelegate<S, A> delegate, S source, A a, Void unused, Void unused2, Void unused3, Void unused4) {
        delegate.invoke(source, a);
    }

    @Override
    public A1BindEvent<L, S, A> forkChild() {
        return new A1BindEvent<>(this);
    }


    class ThisBindHandler extends BindHandler<L> implements Arg1EventDelegate<S, A> {

        public ThisBindHandler(L handler) {
            super(handler);
        }

        @Override
        public void invoke(S source, A a) {
            A1BindEvent.this.invoker.invoke(this.handler, source, a);
        }

    }

}