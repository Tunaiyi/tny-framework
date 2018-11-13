package com.tny.game.net.base;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.event.BindEventBus;
import com.tny.game.common.utils.ExeAide;

import java.lang.reflect.Field;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author: Kun Yang
 * @date: 2018-11-06 15:44
 */
public class BaseEventBuses<L> {

    private volatile List<BindEventBus<L, ?, ?>> eventBuses;

    public BaseEventBuses() {
    }

    protected void init() {
        if (eventBuses != null)
            return;
        synchronized (this) {
            if (eventBuses != null)
                return;
            List<BindEventBus<L, ?, ?>> events = new ArrayList<>();
            for (Field field : this.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (BindEventBus.class.isAssignableFrom(field.getType())) {
                    ExeAide.callUnchecked(() -> (BindEventBus) field.get(this))
                            .ifPresent(eventBus -> events.add(as(eventBus)));
                }
            }
            this.eventBuses = ImmutableList.copyOf(events);
        }
    }

    public void addListener(L listener) {
        this.init();
        eventBuses.forEach(e -> {
            if (e.getBindWith().isInstance(listener))
                e.addListener(listener);
        });
    }

    public void removeListener(L listener) {
        this.init();
        eventBuses.forEach(e -> {
            if (e.getBindWith().isInstance(listener))
                e.removeListener(listener);
        });
    }

    public void clearListener() {
        this.init();
        eventBuses.forEach(BindEventBus::clearListener);
    }

}
