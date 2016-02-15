package com.tny.game.event;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class ComEventBus<E extends Event<?>> extends BaseEventBus<ComEventDelegate<E>> {

    public ComEventBus(List<ComEventDelegate<E>> listeners) {
        super(listeners);
    }

    public void notify(E event) {
        for (ComEventDelegate<E> delegate : listeners) {
            try {
                delegate.invoke(event);
            } catch (Throwable e) {
                LOGGER.error("", e);
            }
        }
    }
}