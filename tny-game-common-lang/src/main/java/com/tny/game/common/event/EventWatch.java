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
 * Created by Kun Yang on 16/2/4.
 */
public interface EventWatch<D> {

    void add(D delegate);

    void remove(D delegate);

    void clear();

    default void add(Collection<? extends D> listeners) {
        listeners.forEach(this::add);
    }

    default void remove(Collection<? extends D> listeners) {
        listeners.forEach(this::remove);
    }
}

