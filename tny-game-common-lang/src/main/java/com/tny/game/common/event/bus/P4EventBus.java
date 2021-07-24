package com.tny.game.common.event.bus;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class P4EventBus<S, A1, A2, A3, A4> extends BaseEventBus<P4EventDelegate<S, A1, A2, A3, A4>> {

    public P4EventBus(List<P4EventDelegate<S, A1, A2, A3, A4>> listeners) {
        super(listeners);
    }

    public void notify(S source, A1 a1, A2 a2, A3 a3, A4 a4) {
        for (P4EventDelegate<S, A1, A2, A3, A4> delegate : this.listeners) {
            try {
                delegate.invoke(source, a1, a2, a3, a4);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

}