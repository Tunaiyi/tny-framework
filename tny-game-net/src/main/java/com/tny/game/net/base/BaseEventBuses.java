package com.tny.game.net.base;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.concurrent.utils.*;
import com.tny.game.common.event.bus.*;

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
        if (this.eventBuses != null) {
            return;
        }
        synchronized (this) {
            if (this.eventBuses != null) {
                return;
            }
            List<BindEventBus<L, ?, ?>> events = new ArrayList<>();
            for (Field field : this.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if (BindEventBus.class.isAssignableFrom(field.getType())) {
                    ExeAide.callUnchecked(() -> (BindEventBus)field.get(this))
                            .ifPresent(eventBus -> events.add(as(eventBus)));
                }
            }
            this.eventBuses = ImmutableList.copyOf(events);
        }
    }

    public void addListener(L listener) {
        this.init();
        this.eventBuses.forEach(e -> {
            if (e.getBindWith().isInstance(listener)) {
                e.addListener(listener);
            }
        });
    }

    public void removeListener(L listener) {
        this.init();
        this.eventBuses.forEach(e -> {
            if (e.getBindWith().isInstance(listener)) {
                e.removeListener(listener);
            }
        });
    }

    public void clearListener() {
        this.init();
        this.eventBuses.forEach(BindEventBus::clearListener);
    }

}
