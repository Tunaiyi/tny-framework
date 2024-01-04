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

import com.google.common.base.Objects;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public abstract class BindEventBus<L, H, D> extends BaseEventBus<D> implements ListenerRegister<L> {

    protected H invoker;

    protected Class<L> bindWith;

    protected boolean global;

    public BindEventBus(Class<L> bindWith, H invoker, List<D> listeners, boolean global) {
        super(listeners);
        this.bindWith = bindWith;
        this.invoker = invoker;
        this.global = global;
    }

    @Override
    public void clearListener() {
        this.listeners.clear();
    }

    public Class<L> getBindWith() {
        return this.bindWith;
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
