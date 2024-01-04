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

import com.tny.game.common.event.*;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class BindComEventBus<L, E extends Event<?>> extends BindEventBus<L, ComEventInvoker<L, E>, ComEventDelegate<E>> {

    public BindComEventBus(Class<L> bindWith, ComEventInvoker<L, E> invoker, List<ComEventDelegate<E>> listeners, boolean global) {
        super(bindWith, invoker, listeners, global);
    }

    @Override
    public void addListener(L handler) {
        this.listeners.add(new ThisBindHandler(handler));
    }

    @Override
    public void removeListener(L handler) {
        this.listeners.remove(new ThisBindHandler(handler));
    }

    public void notify(E event) {
        if (this.global) {
            List<L> global = GlobalListenerHolder.getInstance().getListeners(this.bindWith);
            for (L listener : global) {
                try {
                    this.invoker.invoke(listener, event);
                } catch (Throwable e) {
                    LOGGER.error("", e);
                }
            }
        }
        for (ComEventDelegate<E> delegate : this.listeners) {
            try {
                delegate.invoke(event);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    class ThisBindHandler extends BindHandler<L> implements ComEventDelegate<E> {

        public ThisBindHandler(L handler) {
            super(handler);
        }

        @Override
        public void invoke(E event) {
            BindComEventBus.this.invoker.invoke(this.handler, event);
        }

    }

}