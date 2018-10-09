package com.tny.game.common.event;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public abstract class BindEventBus<L, H, D> extends BaseEventBus<D> implements ListenerRegister<L> {

    protected H invoker;

    protected Class<L> bindWith;

    protected boolean global;

    public BindEventBus(Class<L> bindWith, H invoker, List<D> listeners, boolean global) {
        super(listeners);
        this.bindWith = bindWith;
        this.invoker = invoker;
        this.global = global;
    }

    @Override
    public void clearListener() {
        this.listeners.clear();
    }

    public Class<L> getBindWith() {
        return bindWith;
    }

    protected class BindHandler<L> {

        L handler;

        public BindHandler(L handler) {
            this.handler = handler;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            if (!(o instanceof BindHandler))
                return false;
            BindHandler<?> that = (BindHandler<?>) o;
            return handler != null ? handler.equals(that.handler) : that.handler == null;
        }

        @Override
        public int hashCode() {
            return handler != null ? handler.hashCode() : 0;
        }
    }

}
