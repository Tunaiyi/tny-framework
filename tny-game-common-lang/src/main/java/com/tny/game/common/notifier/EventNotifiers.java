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
