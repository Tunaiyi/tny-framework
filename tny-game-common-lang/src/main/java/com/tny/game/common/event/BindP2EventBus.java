package com.tny.game.common.event;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class BindP2EventBus<L, S, A1, A2> extends BindEventBus<L, P2EventInvoker<L, S, A1, A2>, P2EventDelegate<S, A1, A2>> {

    public BindP2EventBus(Class<L> bindWith, P2EventInvoker<L, S, A1, A2> invoker, List<P2EventDelegate<S, A1, A2>> listeners, boolean global) {
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

    public void notify(S source, A1 a1, A2 a2) {
        if (global) {
            List<L> globalListeners = GlobalListenerHolder.getInstance().getListeners(bindWith);
            for (L listener : globalListeners) {
                try {
                    invoker.invoke(listener, source, a1, a2);
                } catch (Throwable e) {
                    LOGGER.error("", e);
                }
            }
        }
        for (P2EventDelegate<S, A1, A2> delegate : listeners) {
            try {
                delegate.invoke(source, a1, a2);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    class ThisBindHandler extends BindHandler<L> implements P2EventDelegate<S, A1, A2> {

        public ThisBindHandler(L handler) {
            super(handler);
        }

        @Override
        public void invoke(S source, A1 a1, A2 a2) {
            invoker.invoke(handler, source, a1, a2);
        }
    }

}
