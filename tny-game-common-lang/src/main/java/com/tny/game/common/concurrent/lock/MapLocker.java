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

package com.tny.game.common.concurrent.lock;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.*;
import java.util.function.Supplier;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public class MapLocker<K, L extends Lock> {

    private static final MapLocker<Object, Lock> defaultLocker = new MapLocker<>();

    private final Supplier<L> creator;

    private final Map<K, L> lockMap = new ConcurrentHashMap<>();

    public static <K> MapLocker<K, Lock> common() {
        return as(defaultLocker);
    }

    public static <K> MapLocker<K, Lock> newInstance() {
        return new MapLocker<>();
    }

    public static <K, L extends Lock> MapLocker<K, L> newInstance(Supplier<L> creator) {
        return new MapLocker<>(creator);
    }

    private MapLocker(Supplier<L> creator) {
        this.creator = creator;
    }

    private MapLocker() {
        this.creator = () -> as(new ReentrantLock());
    }

    public L getLock(K lockObject) {
        return this.lockMap.computeIfAbsent(lockObject, (k) -> this.creator.get());
    }

}
