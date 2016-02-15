package com.tny.game.event;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class BindP5EventBus<L, S, A1, A2, A3, A4, A5> extends BindEventBus<L, P5EventInvoker<L, S, A1, A2, A3, A4, A5>, P5EventDelegate<S, A1, A2, A3, A4, A5>> {

    public BindP5EventBus(Class<L> bindWith, P5EventInvoker<L, S, A1, A2, A3, A4, A5> invoker, boolean global) {
        super(bindWith, invoker, global);
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
        if (global) {
            List<L> globalListeners = GlobalListenerHolder.getInstance().getListeners(bindWith);
            for (L listener : globalListeners) {
                try {
                    invoker.invoke(listener, source, a1, a2, a3, a4, a5);
                } catch (Throwable e) {
                    LOGGER.error("", e);
                }
            }
        }
        for (P5EventDelegate<S, A1, A2, A3, A4, A5> delegate : listeners) {
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
            invoker.invoke(handler, source, a1, a2, a3, a4, a5);
        }
    }
}