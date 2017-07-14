package com.tny.game.common.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class BaseEventBus<D> implements EventBus<D> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseEventBus.class);

    protected List<D> listeners;

    BaseEventBus(List<D> listeners) {
        if (listeners == null)
            listeners = new CopyOnWriteArrayList<>();
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
