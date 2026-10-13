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
