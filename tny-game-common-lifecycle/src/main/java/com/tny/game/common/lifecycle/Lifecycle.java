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
package com.tny.game.common.lifecycle;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.utils.*;

import java.util.*;

import static com.tny.game.common.utils.ObjectAide.*;
import static com.tny.game.common.utils.StringAide.*;

/**
 * 初始化器
 * Created by Kun Yang on 16/7/24.
 */
@SuppressWarnings("unchecked")
public abstract class Lifecycle<L extends Lifecycle<?, ?>, P extends LifecycleHandler> implements Comparable<Lifecycle<?, ?>> {

    private static final Map<Class<? extends Lifecycle<?, ?>>, Map<Class<? extends LifecycleHandler>, Lifecycle<?, ?>>> INITIATOR_MAP
            = new CopyOnWriteMap<>();

    private Class<L> lifecycleClass;

    private Class<? extends P> processorClass;

    private LifecyclePriority priority;

    private L next;

    private L prev;

    private Lifecycle() {
    }

    private static Map<Class<? extends LifecycleHandler>, Lifecycle<?, ?>> map(Class<? extends Lifecycle<?, ?>> lifecycleClass) {
        Map<Class<? extends LifecycleHandler>, Lifecycle<?, ?>> map = INITIATOR_MAP.get(lifecycleClass);
        if (map == null) {
            return ObjectAide.ifNull(INITIATOR_MAP.putIfAbsent(lifecycleClass, map = new HashMap<>()), map);
        }
        return map;
    }

    static void putLifecycle(Class<? extends Lifecycle<?, ?>> lifecycleClass, Lifecycle<?, ?> lifecycle) {
        Lifecycle<?, ?> old = map(lifecycleClass).putIfAbsent(lifecycle.getHandlerClass(), lifecycle);
        if (old != null) {
            throw new IllegalArgumentException(format("{} 已经存在 {}, 无法添加 {}", lifecycle.getHandlerClass(), old, lifecycle));
        }
    }

    static <I extends Lifecycle<?, ?>> I getLifecycle(Class<? extends Lifecycle<?, ?>> lifecycleClass, Class<?> InitiatorClass) {
        Lifecycle<?, ?> Initiator = map(lifecycleClass).get(InitiatorClass);
        return (I) Initiator;
    }

    Lifecycle(Class<L> lifecycleClass, Class<? extends P> processorClass, LifecyclePriority priority) {
        this.lifecycleClass = lifecycleClass;
        this.processorClass = processorClass;
        this.priority = priority;
    }

    public Class<? extends P> getHandlerClass() {
        return this.processorClass;
    }

    public int getOrder() {
        return this.priority.getOrder();
    }

    public Lifecycle<?, ?> getNext() {
        return this.next;
    }

    public Lifecycle<?, ?> getPrev() {
        return this.prev;
    }

    public L head() {
        if (this.prev == null) {
            return (L) this;
        } else {
            return (L) this.prev.head();
        }
    }

    public L append(L initiator) {
        if (this.next != null) {
            throw new IllegalArgumentException(format("{} next is exist {}", this, this.next));
        }
        if (initiator.getOrder() > this.getOrder()) {
            throw new IllegalArgumentException(format("{} [{}] prior to {} [{}]", initiator, initiator.getOrder(), this, this.getOrder()));
        }
        initiator.setPrev(as(this));
        return this.next = initiator;
    }

    void setPrev(L initiator) {
        if (this.prev != null) {
            throw new IllegalArgumentException(format("{} prev is exist {}", this, this.prev));
        }
        this.prev = initiator;
    }

    public L append(Class<? extends P> clazz) {
        return append(of(clazz));
    }

    protected abstract L of(Class<? extends P> clazz);

    @Override
    public int compareTo(Lifecycle o) {
        int value = o.getOrder() - this.getOrder();
        if (value == 0) {
            return this.processorClass.getName().compareTo(o.processorClass.getName());
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Lifecycle)) {
            return false;
        }
        Lifecycle<?, ?> lifecycle = (Lifecycle<?, ?>) o;
        return lifecycleClass.equals(lifecycle.lifecycleClass) && processorClass.equals(lifecycle.processorClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lifecycleClass, processorClass);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
               "processorClass=" + this.processorClass +
               '}';
    }

}
