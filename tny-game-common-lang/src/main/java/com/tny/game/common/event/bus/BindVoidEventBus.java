package com.tny.game.common.event.bus;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class BindVoidEventBus<L, S> extends BindEventBus<L, VoidEventInvoker<L, S>, VoidEventDelegate<S>> {

    public BindVoidEventBus(Class<L> bindWith, VoidEventInvoker<L, S> invoker, List<VoidEventDelegate<S>> listeners, boolean global) {
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

    public void notify(S source) {
        if (this.global) {
            List<L> global = GlobalListenerHolder.getInstance().getListeners(this.bindWith);
            for (L listener : global) {
                try {
                    this.invoker.invoke(listener, source);
                } catch (Throwable e) {
                    LOGGER.error("", e);
                }
            }
        }
        for (VoidEventDelegate<S> delegate : this.listeners) {
            try {
                delegate.invoke(source);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    class ThisBindHandler extends BindHandler<L> implements VoidEventDelegate<S> {

        public ThisBindHandler(L handler) {
            super(handler);
        }

        @Override
        public void invoke(S source) {
            BindVoidEventBus.this.invoker.invoke(this.handler, source);
        }

    }

}

