package com.tny.game.common.event.bus;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class BindP4EventBus<L, S, A1, A2, A3, A4> extends BindEventBus<L, P4EventInvoker<L, S, A1, A2, A3, A4>, P4EventDelegate<S, A1, A2, A3, A4>> {

    public BindP4EventBus(Class<L> bindWith, P4EventInvoker<L, S, A1, A2, A3, A4> invoker, List<P4EventDelegate<S, A1, A2, A3, A4>> listeners,
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

    public void notify(S source, A1 a1, A2 a2, A3 a3, A4 a4) {
        if (this.global) {
            List<L> comListeners = GlobalListenerHolder.getInstance().getListeners(this.bindWith);
            for (L listener : comListeners) {
                try {
                    this.invoker.invoke(listener, source, a1, a2, a3, a4);
                } catch (Throwable e) {
                    LOGGER.error("", e);
                }
            }
        }
        for (P4EventDelegate<S, A1, A2, A3, A4> delegate : this.listeners) {
            try {
                delegate.invoke(source, a1, a2, a3, a4);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    class ThisBindHandler extends BindHandler<L> implements P4EventDelegate<S, A1, A2, A3, A4> {

        public ThisBindHandler(L handler) {
            super(handler);
        }

        @Override
        public void invoke(S source, A1 a1, A2 a2, A3 a3, A4 a4) {
            BindP4EventBus.this.invoker.invoke(this.handler, source, a1, a2, a3, a4);
        }

    }

}