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

