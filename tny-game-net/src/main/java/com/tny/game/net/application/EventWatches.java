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

import com.tny.game.common.event.*;
import com.tny.game.common.utils.*;

import java.util.stream.Stream;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2018-11-06 15:44
 */
public abstract class EventWatches<L> {

    public EventWatches() {
    }

    protected abstract Stream<EventListen<? extends L>> eventStream();

    private Stream<EventListen<L>> stream() {
        return eventStream().map(ObjectAide::as);
    }

    public void addListener(L listener) {
        stream().forEach(e -> {
            if (e.getListenerClass().isInstance(listener)) {
                e.addListener(listener);
            }
        });
    }

    public void removeListener(L listener) {
        stream().forEach(e -> {
            if (e.getListenerClass().isInstance(listener)) {
                e.removeListener(listener);
            }
        });
    }

    public void clearListener() {
        stream().forEach(EventListen::clearListener);
    }


}
