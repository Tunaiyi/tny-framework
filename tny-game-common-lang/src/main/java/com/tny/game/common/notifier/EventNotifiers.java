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

package com.tny.game.common.notifier;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/19 5:21 下午
 */
public final class EventNotifiers {

    private EventNotifiers() {
    }

    public static <L, S, LE extends L, SE extends S> EventNotifier<LE, SE> notifier(Class<L> clazz) {
        return as(new DefaultEventNotifier<>(clazz, true, CopyOnWriteArrayList::new));
    }

    @SafeVarargs
    public static <L, S, LE extends L, SE extends S> EventNotifier<LE, SE> notifier(Class<L> clazz, L... listeners) {
        return as(new DefaultEventNotifier<>(clazz, true, CopyOnWriteArrayList::new, listeners));
    }

    public static <L, S, LE extends L, SE extends S> EventNotifier<LE, SE> notifier(Class<L> clazz, Collection<LE> listeners) {
        return as(new DefaultEventNotifier<>(clazz, true, CopyOnWriteArrayList::new, listeners));
    }

    public static <L, S, LE extends L, SE extends S> EventNotifier<LE, SE> notifier(Class<L> clazz, Supplier<List<L>> listCreator) {
        return as(new DefaultEventNotifier<>(clazz, true, listCreator));
    }

    @SafeVarargs
    public static <L, S, LE extends L, SE extends S> EventNotifier<LE, SE> notifier(Class<L> clazz, boolean global, L... listeners) {
        return as(new DefaultEventNotifier<>(clazz, global, CopyOnWriteArrayList::new, listeners));
    }

    public static <L, S, LE extends L, SE extends S> EventNotifier<LE, SE> notifier(Class<L> clazz, boolean global, Collection<LE> listeners) {
        return as(new DefaultEventNotifier<>(clazz, global, CopyOnWriteArrayList::new, listeners));
    }

    @SafeVarargs
    public static <L, S, LE extends L, SE extends S> EventNotifier<LE, SE> notifier(Class<L> clazz, Supplier<List<L>> listCreator, L... listeners) {
        return as(new DefaultEventNotifier<>(clazz, true, listCreator, listeners));
    }

    public static <L, S, LE extends L, SE extends S> EventNotifier<LE, SE> notifier(Class<L> clazz, Supplier<List<L>> listCreator,
            Collection<L> listeners) {
        return as(new DefaultEventNotifier<>(clazz, true, listCreator, listeners));
    }

    public static <L, S, LE extends L, SE extends S> EventNotifier<LE, SE> notifier(Class<L> clazz, boolean global, Supplier<List<L>> listCreator) {
        return as(new DefaultEventNotifier<>(clazz, global, listCreator));
    }

    @SafeVarargs
    public static <L, S, LE extends L, SE extends S> EventNotifier<LE, SE> notifier(Class<L> clazz, boolean global, Supplier<List<L>> listCreator,
            L... listeners) {
        return as(new DefaultEventNotifier<>(clazz, global, listCreator, listeners));
    }

    public static <L, S, LE extends L, SE extends S> EventNotifier<LE, SE> notifier(Class<L> clazz, boolean global, Supplier<List<L>> listCreator,
            Collection<LE> listeners) {
        return as(new DefaultEventNotifier<>(clazz, global, listCreator, listeners));
    }

}
