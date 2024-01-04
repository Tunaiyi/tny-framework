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

package com.tny.game.common.scheduler;

import java.util.*;

/**
 * Created by Kun Yang on 2017/6/13.
 */
public class TriggerContext {

    private Collection<TimeTaskEvent> events;

    public TriggerContext(Collection<TimeTaskEvent> events) {
        this.events = Collections.unmodifiableCollection(events);
    }

    public int countHandler(Class<? extends TimeTaskHandler> handleClass) {
        return this.events.stream()
                .mapToInt(e -> {
                    int times = 0;
                    for (TimeTaskHandler h : e.getHandlerList()) {
                        if (handleClass.isInstance(h)) {
                            times++;
                        }
                    }
                    return times;
                })
                .sum();
    }

    public int countHandler(TimeTaskHandler handler) {
        return this.events.stream()
                .mapToInt(e -> {
                    int times = 0;
                    for (TimeTaskHandler h : e.getHandlerList()) {
                        if (h.equals(handler)) {
                            times++;
                        }
                    }
                    return times;
                })
                .sum();
    }

}
