package com.tny.game.common.event;

import java.util.*;
import java.util.function.Supplier;

public abstract class ArgsBindEvent<L, H, D, S, A1, A2, A3, A4, A5, E extends ArgsBindEvent<L, H, D, S, A1, A2, A3, A4, A5, E>>
        extends BindEvent<L, H, D, E> {

    protected ArgsBindEvent(E parent) {
        super(parent);
    }

    protected ArgsBindEvent(Class<L> bindClass, H invoker, boolean global) {
        super(bindClass, invoker, global);
    }

    protected ArgsBindEvent(Class<L> bindWith, H invoker, Supplier<Collection<D>> factory, boolean global) {
        super(bindWith, invoker, factory, global);
    }

    protected final void doNotify(S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        if (parent != null) {
            doParentNotify(parent, source, a1, a2, a3, a4, a5);
        }
        if (this.global) {
            List<L> comListeners = GlobalListenerHolder.getInstance().getListeners(this.bindClass);
            for (L listener : comListeners) {
                try {
                    doListenerNotify(listener, source, a1, a2, a3, a4, a5);
                } catch (Throwable e) {
                    LOGGER.error("", e);
                }
            }
        }
        for (D delegate : this.readOnlyListeners()) {
            try {
                doDelegateNotify(delegate, source, a1, a2, a3, a4, a5);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

    public abstract void doParentNotify(E parent, S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5);

    public abstract void doListenerNotify(L listener, S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5);

    public abstract void doDelegateNotify(D delegate, S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5);

}
