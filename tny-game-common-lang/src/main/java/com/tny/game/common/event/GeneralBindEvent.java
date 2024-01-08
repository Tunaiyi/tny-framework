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
public class GeneralBindEvent<L, N extends EventNotice<?>>
        extends ArgsBindEvent<L, GeneralEventInvoker<L, N>, GeneralEventDelegate<N>, N, Void, Void, Void, Void, Void, GeneralBindEvent<L, N>> {

    public GeneralBindEvent(GeneralBindEvent<L, N> parent) {
        super(parent);
    }

    public GeneralBindEvent(Class<L> bindClass, GeneralEventInvoker<L, N> invoker, boolean global) {
        super(bindClass, invoker, global);
    }

    public GeneralBindEvent(Class<L> bindWith, GeneralEventInvoker<L, N> invoker, Supplier<Collection<GeneralEventDelegate<N>>> factory, boolean global) {
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

    public void notify(N notice) {
        doNotify(notice, null, null, null, null, null);
    }


    @Override
    public void doParentNotify(GeneralBindEvent<L, N> parent, N source, Void unused, Void unused2, Void unused3, Void unused4, Void unused5) {
        parent.notify(source);
    }

    @Override
    public void doListenerNotify(L listener, N source, Void unused, Void unused2, Void unused3, Void unused4, Void unused5) {
        this.invoker.invoke(listener, source);
    }

    @Override
    public void doDelegateNotify(GeneralEventDelegate<N> delegate, N source, Void unused, Void unused2, Void unused3, Void unused4, Void unused5) {
        delegate.invoke(source);
    }

    @Override
    public GeneralBindEvent<L, N> forkChild() {
        return new GeneralBindEvent<>(this);
    }

    class ThisBindHandler extends BindHandler<L> implements GeneralEventDelegate<N> {

        public ThisBindHandler(L handler) {
            super(handler);
        }

        @Override
        public void invoke(N notice) {
            GeneralBindEvent.this.invoker.invoke(this.handler, notice);
        }
    }

}