package com.tny.game.common.event.bus;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class VoidEventBus<S> extends BaseEventBus<VoidEventDelegate<S>> {

    public VoidEventBus(List<VoidEventDelegate<S>> listeners) {
        super(listeners);
    }

    public void notify(S source) {
        for (VoidEventDelegate<S> delegate : this.listeners) {
            try {
                delegate.invoke(source);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }

}