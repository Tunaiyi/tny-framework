package com.tny.game.lifecycle;

import com.tny.game.LogUtils;
import com.tny.game.common.reflect.ObjectUtils;
import com.tny.game.common.utils.collection.CopyOnWriteMap;

import java.util.HashMap;
import java.util.Map;

/**
 * 初始化器
 * Created by Kun Yang on 16/7/24.
 */
@SuppressWarnings("unchecked")
public abstract class Lifecycle<L extends Lifecycle, P extends LifecycleHandler> {

    private static Map<Class<? extends Lifecycle>, Map<Class<? extends LifecycleHandler>, Lifecycle>> initerMap = new CopyOnWriteMap<>();

    private Class<? extends P> processorClass;
    private LifecyclePriority priority;
    private L next;
    private L prev;

    Lifecycle() {
    }

    private static Map<Class<? extends LifecycleHandler>, Lifecycle> map(Class<? extends Lifecycle> lifecycleClass) {
        Map<Class<? extends LifecycleHandler>, Lifecycle> map = initerMap.get(lifecycleClass);
        if (map == null)
            return ObjectUtils.defaultIfNull(initerMap.putIfAbsent(lifecycleClass, map = new HashMap<>()), map);
        return map;
    }

    static void putLifecycle(Class<? extends Lifecycle> lifecycleClass, Lifecycle<?, ?> initer) {
        Lifecycle old = map(lifecycleClass).put(initer.getHandlerClass(), initer);
        if (old != null)
            throw new IllegalArgumentException(LogUtils.format("{} 已经存在 {}, 无法添加 {}", initer.getHandlerClass(), old, initer));
    }

    static <I extends Lifecycle> I getLifecycle(Class<? extends Lifecycle> lifecycleClass, Class<?> initerClass) {
        Lifecycle initer = map(lifecycleClass).get(initerClass);
        return (I) initer;
    }

    Lifecycle(Class<? extends P> processorClass, LifecyclePriority priority) {
        this.processorClass = processorClass;
        this.priority = priority;
    }

    public Class<? extends P> getHandlerClass() {
        return processorClass;
    }

    public int getPriority() {
        return priority.getPriority();
    }

    public L getNext() {
        return next;
    }

    public L getPrev() {
        return prev;
    }

    public L head() {
        if (this.prev == null)
            return (L) this;
        else
            return (L) prev.head();
    }

    public L append(L initer) {
        if (next != null)
            throw new IllegalArgumentException(LogUtils.format("{} next is exist {}", this, this.next));
        if (initer.getPriority() > this.getPriority())
            throw new IllegalArgumentException(LogUtils.format("{} [{}] prior to {} [{}]", initer, initer.getPriority(), this, this.getPriority()));
        initer.setPrev(this);
        return next = initer;
    }

    public L append(Class<? extends P> clazz) {
        return append(of(clazz));
    }

    protected abstract L of(Class<? extends P> clazz);

    void setPrev(L initer) {
        if (prev != null)
            throw new IllegalArgumentException(LogUtils.format("{} prev is exist {}", this, this.prev));
        this.prev = initer;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "processorClass=" + processorClass +
                '}';
    }
}
