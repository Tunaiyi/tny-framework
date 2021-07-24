package com.tny.game.common.event.bus;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class P1EventBus<S, A> extends BaseEventBus<P1EventDelegate<S, A>> {

    public P1EventBus(List<P1EventDelegate<S, A>> listeners) {
        super(listeners);
    }

    public void notify(S source, A a) {
        for (P1EventDelegate<S, A> delegate : this.listeners) {
            try {
                delegate.invoke(source, a);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

}