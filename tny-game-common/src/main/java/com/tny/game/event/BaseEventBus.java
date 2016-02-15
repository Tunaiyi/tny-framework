package com.tny.game.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Kun Yang on 16/2/4.
 */
class BaseEventBus<D> implements EventBus<D> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseEventBus.class);

    protected List<D> listeners;

    BaseEventBus(List<D> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void add(D delegate) {
        listeners.add(delegate);
    }

    @Override
    public void remove(D delegate) {
        listeners.remove(delegate);
    }

    @Override
    public void clear() {
        listeners.clear();
    }

}
