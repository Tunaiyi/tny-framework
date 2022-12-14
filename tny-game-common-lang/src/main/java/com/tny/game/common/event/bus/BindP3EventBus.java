/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.event.bus;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class BindP3EventBus<L, S, A1, A2, A3> extends BindEventBus<L, P3EventInvoker<L, S, A1, A2, A3>, P3EventDelegate<S, A1, A2, A3>> {

    public BindP3EventBus(Class<L> bindWith, P3EventInvoker<L, S, A1, A2, A3> invoker, List<P3EventDelegate<S, A1, A2, A3>> listeners,
            boolean global) {
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

    public void notify(S source, A1 a1, A2 a2, A3 a3) {
        if (this.global) {
            List<L> globalListeners = GlobalListenerHolder.getInstance().getListeners(this.bindWith);
            for (L listener : globalListeners) {
                try {
                    this.invoker.invoke(listener, source, a1, a2, a3);
                } catch (Throwable e) {
                    LOGGER.error("", e);
                }
            }
        }
        for (P3EventDelegate<S, A1, A2, A3> delegate : this.listeners) {
            try {
                delegate.invoke(source, a1, a2, a3);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    class ThisBindHandler extends BindHandler<L> implements P3EventDelegate<S, A1, A2, A3> {

        public ThisBindHandler(L handler) {
            super(handler);
        }

        @Override
        public void invoke(S source, A1 a1, A2 a2, A3 a3) {
            BindP3EventBus.this.invoker.invoke(this.handler, source, a1, a2, a3);
        }

    }

}