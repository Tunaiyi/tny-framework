package com.tny.game.common.event.bus;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class BindP1EventBus<L, S, A> extends BindEventBus<L, P1EventInvoker<L, S, A>, P1EventDelegate<S, A>> {

    public BindP1EventBus(Class<L> bindWith, P1EventInvoker<L, S, A> invoker, List<P1EventDelegate<S, A>> listeners, boolean global) {
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

    public void notify(S source, A a) {
        if (this.global) {
            List<L> comListeners = GlobalListenerHolder.getInstance().getListeners(this.bindWith);
            for (L listener : comListeners) {
                try {
                    this.invoker.invoke(listener, source, a);
                } catch (Throwable e) {
                    LOGGER.error("", e);
                }
            }
        }
        for (P1EventDelegate<S, A> delegate : this.listeners) {
            try {
                delegate.invoke(source, a);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    class ThisBindHandler extends BindHandler<L> implements P1EventDelegate<S, A> {

        public ThisBindHandler(L handler) {
            super(handler);
        }

        @Override
        public void invoke(S source, A a) {
            BindP1EventBus.this.invoker.invoke(this.handler, source, a);
        }

    }

}