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

import com.tny.game.common.collection.empty.*;
import com.tny.game.common.event.*;
import org.slf4j.*;

import java.util.*;
import java.util.function.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/19 3:30 下午
 */
class DefaultEventNotifier<L, S> implements EventNotifier<L, S> {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultEventNotifier.class);

    private final Class<L> listenerClass;

    private final List<L> listenerList;

    private final boolean global;

    public DefaultEventNotifier(Class<L> listenerClass, boolean global, Supplier<List<L>> listCreator) {
        this.listenerClass = listenerClass;
        this.global = global;
        this.listenerList = new EmptyImmutableList<>(listCreator);
    }

    @SafeVarargs
    public DefaultEventNotifier(Class<L> listenerClass, boolean global, Supplier<List<L>> listCreator, L... listeners) {
        this(listenerClass, global, listCreator);
        if (listeners.length == 1) {
            this.listenerList.add(listeners[0]);
        }
        if (listeners.length > 1) {
            this.listenerList.addAll(Arrays.asList(listeners));
        }
    }

    public DefaultEventNotifier(Class<L> listenerClass, boolean global, Supplier<List<L>> listCreator,
            Collection<? extends L> listeners) {
        this(listenerClass, global, listCreator);
        if (!listeners.isEmpty()) {
            this.listenerList.addAll(listeners);
        }
    }

    @SafeVarargs
    public DefaultEventNotifier(Class<L> listenerClass, boolean global, L... listeners) {
        this(listenerClass, global, ArrayList::new, listeners);
    }

    public DefaultEventNotifier(Class<L> listenerClass, boolean global, Collection<? extends L> listeners) {
        this(listenerClass, global, ArrayList::new, listeners);
    }

    @Override
    public void add(L listener) {
        this.listenerList.add(listener);
    }

    @Override
    public void add(Collection<? extends L> listeners) {
        this.listenerList.addAll(listeners);
    }

    @Override
    public void remove(L listener) {
        this.listenerList.remove(listener);
    }

    @Override
    public void remove(Collection<? extends L> listeners) {
        this.listenerList.removeAll(listeners);
    }

    @Override
    public void clear() {
        this.listenerList.clear();
    }

    @Override
    public void notify(BiConsumer<L, S> invoker, S source) {
        this.doTrigger(invoker, (i, listener) -> i.accept(listener, source));
    }

    // @Override
    // public <E extends Event<S>> void fire(ComEventInvoker<L, E> invoker, E event) {
    //     this.doTrigger(invoker, (i, listener) -> i.invoke(listener, event));
    // }

    @Override
    public <A> void notify(Arg1EventInvoker<L, S, A> invoker, S source, A a) {
        this.doTrigger(invoker, (i, listener) -> i.invoke(listener, source, a));
    }

    @Override
    public <A1, A2> void notify(Args2EventInvoker<L, S, A1, A2> invoker, S source, A1 a1, A2 a2) {
        this.doTrigger(invoker, (i, listener) -> i.invoke(listener, source, a1, a2));
    }

    @Override
    public <A1, A2, A3> void notify(Args3EventInvoker<L, S, A1, A2, A3> invoker, S source, A1 a1, A2 a2, A3 a3) {
        this.doTrigger(invoker, (i, listener) -> i.invoke(listener, source, a1, a2, a3));
    }

    @Override
    public <A1, A2, A3, A4> void notify(Args4EventInvoker<L, S, A1, A2, A3, A4> invoker, S source, A1 a1, A2 a2, A3 a3, A4 a4) {
        this.doTrigger(invoker, (i, listener) -> i.invoke(listener, source, a1, a2, a3, a4));
    }

    @Override
    public <A1, A2, A3, A4, A5> void notify(Args5EventInvoker<L, S, A1, A2, A3, A4, A5> invoker, S source, A1 a1, A2 a2, A3 a3, A4 a4, A5 a5) {
        this.doTrigger(invoker, (i, listener) -> i.invoke(listener, source, a1, a2, a3, a4, a5));
    }

    private <I> void doTrigger(I invoker, BiConsumer<I, L> fire) {
        if (this.global) {
            List<L> globalListeners = GlobalListenerHolder.getInstance().getListeners(this.listenerClass);
            for (L listener : globalListeners) {
                try {
                    fire.accept(invoker, listener);
                } catch (Throwable e) {
                    LOGGER.error("{} listener trigger {} exception", listener, invoker.getClass(), e);
                }
            }
        }
        for (L listener : this.listenerList) {
            try {
                fire.accept(invoker, listener);
            } catch (Throwable e) {
                LOGGER.error("{} listener trigger {} exception", listener, invoker.getClass(), e);
            }
        }
    }

}
