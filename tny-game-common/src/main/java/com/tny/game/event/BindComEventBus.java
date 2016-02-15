package com.tny.game.event;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class BindComEventBus<L, E extends Event<?>> extends BindEventBus<L, ComEventInvoker<L, E>, ComEventDelegate<E>> {

    public BindComEventBus(Class<L> bindWith, ComEventInvoker<L, E> invoker, boolean global) {
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

    public void notify(E event) {
        if (global) {
            List<L> globalListeners = GlobalListenerHolder.getInstance().getListeners(bindWith);
            for (L listener : globalListeners) {
                try {
                    invoker.invoke(listener, event);
                } catch (Throwable e) {
                    LOGGER.error("", e);
                }
            }
        }
        for (ComEventDelegate<E> delegate : listeners) {
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
            invoker.invoke(handler, event);
        }

    }

}