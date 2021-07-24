package com.tny.game.common.event.bus;

import org.slf4j.*;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Kun Yang on 16/2/4.
 */
public class BaseEventBus<D> implements EventBus<D> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseEventBus.class);

    protected List<D> listeners;

    BaseEventBus(List<D> listeners) {
        if (listeners == null || listeners.isEmpty()) {
            listeners = new CopyOnWriteArrayList<>();
        }
        this.listeners = new CopyOnWriteArrayList<>(listeners);
    }

    @Override
    public void add(D listener) {
        this.listeners.add(listener);
    }

    @Override
    public void remove(D listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void clear() {
        this.listeners.clear();
    }

}
