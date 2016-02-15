package com.tny.game.event;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class P5EventBus<S, A1, A2, A3, A4, A5> extends BaseEventBus<P5EventDelegate<S, A1, A2, A3, A4, A5>> {

    public P5EventBus(List<P5EventDelegate<S, A1, A2, A3, A4, A5>> listeners) {
        super(listeners);
    }

    public void notify(S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        for (P5EventDelegate<S, A1, A2, A3, A4, A5> delegate : listeners) {
            try {
                delegate.invoke(source, a1, a2, a3, a4, a5);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

}