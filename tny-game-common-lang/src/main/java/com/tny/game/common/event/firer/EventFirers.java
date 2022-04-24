package com.tny.game.common.event.firer;

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
public final class EventFirers {

    private EventFirers() {
    }

    public static <L, S, LE extends L, SE extends S> EventFirer<LE, SE> firer(Class<L> clazz) {
        return as(new DefaultEventFirer<>(clazz, true, CopyOnWriteArrayList::new));
    }

    @SafeVarargs
    public static <L, S, LE extends L, SE extends S> EventFirer<LE, SE> firer(Class<L> clazz, L... listeners) {
        return as(new DefaultEventFirer<>(clazz, true, CopyOnWriteArrayList::new, listeners));
    }

    public static <L, S, LE extends L, SE extends S> EventFirer<LE, SE> firer(Class<L> clazz, Collection<LE> listeners) {
        return as(new DefaultEventFirer<>(clazz, true, CopyOnWriteArrayList::new, listeners));
    }

    public static <L, S, LE extends L, SE extends S> EventFirer<LE, SE> firer(Class<L> clazz, Supplier<List<L>> listCreator) {
        return as(new DefaultEventFirer<>(clazz, true, listCreator));
    }

    @SafeVarargs
    public static <L, S, LE extends L, SE extends S> EventFirer<LE, SE> firer(Class<L> clazz, boolean global, L... listeners) {
        return as(new DefaultEventFirer<>(clazz, global, CopyOnWriteArrayList::new, listeners));
    }

    public static <L, S, LE extends L, SE extends S> EventFirer<LE, SE> firer(Class<L> clazz, boolean global, Collection<LE> listeners) {
        return as(new DefaultEventFirer<>(clazz, global, CopyOnWriteArrayList::new, listeners));
    }

    @SafeVarargs
    public static <L, S, LE extends L, SE extends S> EventFirer<LE, SE> firer(Class<L> clazz, Supplier<List<L>> listCreator, L... listeners) {
        return as(new DefaultEventFirer<>(clazz, true, listCreator, listeners));
    }

    public static <L, S, LE extends L, SE extends S> EventFirer<LE, SE> firer(Class<L> clazz, Supplier<List<L>> listCreator,
            Collection<L> listeners) {
        return as(new DefaultEventFirer<>(clazz, true, listCreator, listeners));
    }

    public static <L, S, LE extends L, SE extends S> EventFirer<LE, SE> firer(Class<L> clazz, boolean global, Supplier<List<L>> listCreator) {
        return as(new DefaultEventFirer<>(clazz, global, listCreator));
    }

    @SafeVarargs
    public static <L, S, LE extends L, SE extends S> EventFirer<LE, SE> trigger(Class<L> clazz, boolean global, Supplier<List<L>> listCreator,
            L... listeners) {
        return as(new DefaultEventFirer<>(clazz, global, listCreator, listeners));
    }

    public static <L, S, LE extends L, SE extends S> EventFirer<LE, SE> trigger(Class<L> clazz, boolean global, Supplier<List<L>> listCreator,
            Collection<LE> listeners) {
        return as(new DefaultEventFirer<>(clazz, global, listCreator, listeners));
    }

}
