package com.tny.game.common.event;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class P2EventBus<S, A1, A2> extends BaseEventBus<P2EventDelegate<S, A1, A2>> {

    public P2EventBus(List<P2EventDelegate<S, A1, A2>> listeners) {
        super(listeners);
    }

    public void notify(S source, A1 a1, A2 a2) {
        for (P2EventDelegate<S, A1, A2> delegate : listeners) {
            try {
                delegate.invoke(source, a1, a2);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }
}