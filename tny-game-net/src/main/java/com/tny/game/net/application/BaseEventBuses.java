/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.net.application;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.concurrent.utils.*;
import com.tny.game.common.event.bus.*;

import java.lang.reflect.Field;
import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-11-06 15:44
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
                    ExeAide.callUnchecked(() -> (BindEventBus) field.get(this))
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
