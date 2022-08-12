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
public class BindP5EventBus<L, S, A1, A2, A3, A4, A5>
        extends BindEventBus<L, P5EventInvoker<L, S, A1, A2, A3, A4, A5>, P5EventDelegate<S, A1, A2, A3, A4, A5>> {

    public BindP5EventBus(Class<L> bindWith, P5EventInvoker<L, S, A1, A2, A3, A4, A5> invoker, List<P5EventDelegate<S, A1, A2, A3, A4, A5>> listeners,
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

    public void notify(S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        if (this.global) {
            List<L> globalListeners = GlobalListenerHolder.getInstance().getListeners(this.bindWith);
            for (L listener : globalListeners) {
                try {
                    this.invoker.invoke(listener, source, a1, a2, a3, a4, a5);
                } catch (Throwable e) {
                    LOGGER.error("", e);
                }
            }
        }
        for (P5EventDelegate<S, A1, A2, A3, A4, A5> delegate : this.listeners) {
            try {
                delegate.invoke(source, a1, a2, a3, a4, a5);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    class ThisBindHandler extends BindHandler<L> implements P5EventDelegate<S, A1, A2, A3, A4, A5> {

        public ThisBindHandler(L handler) {
            super(handler);
        }

        @Override
        public void invoke(S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
            BindP5EventBus.this.invoker.invoke(this.handler, source, a1, a2, a3, a4, a5);
        }

    }

}