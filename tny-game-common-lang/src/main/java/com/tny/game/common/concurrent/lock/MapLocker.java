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
