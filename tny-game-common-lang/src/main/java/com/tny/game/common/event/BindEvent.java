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

import com.google.common.base.Objects;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/2/4.
 */
public abstract class BindEvent<L, H, D, E extends BindEvent<L, H, D, E>> extends BaseEvent<D, E> implements EventListen<L> {

    protected H invoker;

    protected Class<L> bindClass;

    protected boolean global;

    public BindEvent(Class<L> bindClass, H invoker, boolean global) {
        this(bindClass, invoker, null, global);
    }

    protected BindEvent(E parent) {
        super(parent);
        this.invoker = parent.invoker;
        this.bindClass = parent.bindClass;
        this.global = false;
    }

    public BindEvent(Class<L> bindClass, H invoker, Supplier<Collection<D>> factory, boolean global) {
        super(factory);
        this.bindClass = bindClass;
        this.invoker = invoker;
        this.global = global;
    }

    @Override
    public void clearListener() {
        this.clear();
    }


    @Override
    public Class<?> getListenerClass() {
        return bindClass;
    }

    protected static class BindHandler<L> {

        L handler;

        public BindHandler(L handler) {
            this.handler = handler;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            BindHandler<?> that = (BindHandler<?>) o;
            return Objects.equal(handler, that.handler);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(handler);
        }
    }

}
