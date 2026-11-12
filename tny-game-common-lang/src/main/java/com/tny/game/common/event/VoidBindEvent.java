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
public class VoidBindEvent<L, S> extends ArgsBindEvent<L,
        VoidEventInvoker<L, S>,
        VoidEventDelegate<S>,
        S, Void, Void, Void, Void, Void,
        VoidBindEvent<L, S>> {

    public VoidBindEvent(VoidBindEvent<L, S> parent) {
        super(parent);
    }

    public VoidBindEvent(Class<L> bindClass, VoidEventInvoker<L, S> invoker, boolean global) {
        super(bindClass, invoker, global);
    }

    public VoidBindEvent(Class<L> bindWith, VoidEventInvoker<L, S> invoker, Supplier<Collection<VoidEventDelegate<S>>> factory,
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

    public void notify(S source) {
        doNotify(source, null, null, null, null, null);
    }

    @Override
    public void doParentNotify(VoidBindEvent<L, S> parent, S source, Void unused, Void unused2, Void unused3, Void unused4, Void unused5) {
        parent.notify(source);
    }

    @Override
    public void doListenerNotify(L listener, S source, Void unused, Void unused2, Void unused3, Void unused4, Void unused5) {
        this.invoker.invoke(listener, source);
    }

    @Override
    public void doDelegateNotify(VoidEventDelegate<S> delegate, S source, Void unused, Void unused2, Void unused3, Void unused4, Void unused5) {
        delegate.invoke(source);
    }

    @Override
    public VoidBindEvent<L, S> forkChild() {
        return new VoidBindEvent<>(this);
    }

    class ThisBindHandler extends BindHandler<L> implements VoidEventDelegate<S> {

        public ThisBindHandler(L handler) {
            super(handler);
        }

        @Override
        public void invoke(S source) {
            VoidBindEvent.this.invoker.invoke(this.handler, source);
        }

    }

}

