package com.tny.game.event;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class P3EventBus<S, A1, A2, A3> extends BaseEventBus<P3EventDelegate<S, A1, A2, A3>> {

    public P3EventBus(List<P3EventDelegate<S, A1, A2, A3>> listeners) {
        super(listeners);
    }

    public void notify(S source, A1 a1, A2 a2, A3 a3) {
        for (P3EventDelegate<S, A1, A2, A3> delegate : listeners) {
            try {
                delegate.invoke(source, a1, a2, a3);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }
}