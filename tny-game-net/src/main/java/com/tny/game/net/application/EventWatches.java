/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
