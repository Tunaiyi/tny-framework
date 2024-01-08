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

package com.tny.game.common.event;

import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * Created by Kun Yang on 16/2/4.
 */
public abstract class BaseEvent<D, E extends BaseEvent<D, E>> implements Event<D, E> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseEvent.class);

    protected E parent;

    protected Supplier<Collection<D>> factory;

    private volatile Collection<D> listeners;

    protected BaseEvent() {
    }

    protected BaseEvent(E parent) {
        this(ConcurrentLinkedQueue::new);
        this.parent = parent;
        this.factory = parent.factory;
    }

    protected BaseEvent(Supplier<Collection<D>> factory) {
        this.factory = factory;
    }

    protected Optional<Collection<D>> listenerOpt() {
        return Optional.ofNullable(listeners);
    }

    protected Collection<D> readOnlyListeners() {
        return listeners == null ? List.of() : listeners;
    }

    protected Collection<D> loadListeners() {
        if (listeners != null) {
            return listeners;
        }
        synchronized (this) {
            if (listeners != null) {
                return listeners;
            }
            listeners = factory != null ? factory.get() : new CopyOnWriteArrayList<>();
        }
        return listeners;
    }


    @Override
    public void add(D listener) {
        if (listener == null) {
            return;
        }
        this.loadListeners().add(listener);
    }

    @Override
    public void remove(D listener) {
        if (listener == null) {
            return;
        }
        var opt = this.listenerOpt();
        if (opt.isPresent()) {
            var listeners = opt.get();
            listeners.remove(listener);
        }
    }

    @Override
    public void clear() {
        var opt = this.listenerOpt();
        if (opt.isPresent()) {
            var listeners = opt.get();
            listeners.clear();
        }
    }

}

