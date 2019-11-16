package com.tny.game.common.lifecycle;

import com.tny.game.common.collection.*;
import com.tny.game.common.utils.*;

import java.util.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * 初始化器
 * Created by Kun Yang on 16/7/24.
 */
@SuppressWarnings("unchecked")
public abstract class Lifecycle<L extends Lifecycle, P extends LifecycleHandler> implements Comparable<Lifecycle> {

    private static Map<Class<? extends Lifecycle>, Map<Class<? extends LifecycleHandler>, Lifecycle>> InitiatorMap = new CopyOnWriteMap<>();

    private Class<? extends P> processorClass;
    private LifecyclePriority priority;
    private L next;
    private L prev;

    Lifecycle() {
    }

    private static Map<Class<? extends LifecycleHandler>, Lifecycle> map(Class<? extends Lifecycle> lifecycleClass) {
        Map<Class<? extends LifecycleHandler>, Lifecycle> map = InitiatorMap.get(lifecycleClass);
        if (map == null)
            return ObjectAide.defaultIfNull(InitiatorMap.putIfAbsent(lifecycleClass, map = new HashMap<>()), map);
        return map;
    }

    static void putLifecycle(Class<? extends Lifecycle> lifecycleClass, Lifecycle<?, ?> Initiator) {
        Lifecycle old = map(lifecycleClass).put(Initiator.getHandlerClass(), Initiator);
        if (old != null)
            throw new IllegalArgumentException(format("{} 已经存在 {}, 无法添加 {}", Initiator.getHandlerClass(), old, Initiator));
    }

    static <I extends Lifecycle> I getLifecycle(Class<? extends Lifecycle> lifecycleClass, Class<?> InitiatorClass) {
        Lifecycle Initiator = map(lifecycleClass).get(InitiatorClass);
        return (I) Initiator;
    }

    Lifecycle(Class<? extends P> processorClass, LifecyclePriority priority) {
        this.processorClass = processorClass;
        this.priority = priority;
    }

    public Class<? extends P> getHandlerClass() {
        return this.processorClass;
    }

    public int getPriority() {
        return this.priority.getPriority();
    }

    public L getNext() {
        return this.next;
    }

    public L getPrev() {
        return this.prev;
    }

    public L head() {
        if (this.prev == null)
            return (L) this;
        else
            return (L) this.prev.head();
    }

    public L append(L Initiator) {
        if (this.next != null)
            throw new IllegalArgumentException(format("{} next is exist {}", this, this.next));
        if (Initiator.getPriority() > this.getPriority())
            throw new IllegalArgumentException(format("{} [{}] prior to {} [{}]", Initiator, Initiator.getPriority(), this, this.getPriority()));
        Initiator.setPrev(this);
        return this.next = Initiator;
    }

    public L append(Class<? extends P> clazz) {
        return append(of(clazz));
    }

    protected abstract L of(Class<? extends P> clazz);

    @Override
    public int compareTo(Lifecycle o) {
        int value = o.getPriority() - this.getPriority();
        if (value == 0)
            return this.processorClass.getName().compareTo(o.processorClass.getName());
        return value;
    }

    void setPrev(L Initiator) {
        if (this.prev != null)
            throw new IllegalArgumentException(format("{} prev is exist {}", this, this.prev));
        this.prev = Initiator;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
               "processorClass=" + this.processorClass +
               '}';
    }
}
