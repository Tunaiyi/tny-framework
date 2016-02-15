package com.tny.game.event;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Kun Yang on 16/2/4.
 */
abstract class BindEventBus<L, H, D> extends BaseEventBus<D> {

    protected H invoker;

    protected Class<L> bindWith;

    protected boolean global;

    public BindEventBus(Class<L> bindWith, H invoker, boolean global) {
        super(new CopyOnWriteArrayList<>());
        this.bindWith = bindWith;
        this.invoker = invoker;
        this.global = global;
//        this.global = global;
    }

    public abstract void addListener(L handler);

    public abstract void removeListener(L handler);

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
