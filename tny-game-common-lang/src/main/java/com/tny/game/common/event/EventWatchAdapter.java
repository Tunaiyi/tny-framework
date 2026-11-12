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

package com.tny.game.common.event;

import java.util.Collection;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/19 3:42 下午
 */
public interface EventWatchAdapter<L> extends EventWatch<L> {

    EventWatch<L> eventWatch();

    @Override
    default void add(L listener) {
        eventWatch().add(listener);
    }

    @Override
    default void remove(L listener) {
        eventWatch().remove(listener);
    }

    @Override
    default void add(Collection<? extends L> listeners) {
        eventWatch().add(listeners);
    }

    @Override
    default void remove(Collection<? extends L> listeners) {
        eventWatch().remove(listeners);
    }

    @Override
    default void clear() {
        eventWatch().clear();
    }

}
