package com.tny.game.event;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class BindVoidEventBus<L, S> extends BindEventBus<L, VoidEventInvoker<L, S>, VoidEventDelegate<S>> {

    public BindVoidEventBus(Class<L> bindWith, VoidEventInvoker<L, S> invoker, boolean global) {
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

    public void notify(S source) {
        if (global) {
            List<L> global = GlobalListenerHolder.getInstance().getListeners(bindWith);
            for (L listener : global) {
                try {
                    invoker.invoke(listener, source);
                } catch (Throwable e) {
                    LOGGER.error("", e);
                }
            }
        }
        for (VoidEventDelegate<S> delegate : listeners) {
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
            invoker.invoke(handler, source);
        }

    }

}

