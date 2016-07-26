package com.tny.game.net.initer;

import com.tny.game.LogUtils;
import com.tny.game.common.utils.collection.CopyOnWriteMap;

import java.util.Map;

/**
 * 初始化器
 * Created by Kun Yang on 16/7/24.
 */
@SuppressWarnings("unchecked")
public class StartIniter<P extends StartIniter> {

    private static Map<Class<?>, StartIniter> initerMap = new CopyOnWriteMap<>();

    private Class<?> initerClass;
    private InitLevel initLevel;
    private P next;
    private P prev;

    StartIniter() {
    }

    static void putIniter(StartIniter<?> initer) {
        StartIniter old = initerMap.put(initer.getIniterClass(), initer);
        if (old != null)
            throw new IllegalArgumentException(LogUtils.format("{} 已经存在 {}, 无法添加 {}", initer.getIniterClass(), old, initer));
    }

    static <I extends StartIniter> I getIniter(Class<?> initerClass) {
        StartIniter initer = initerMap.get(initerClass);
        return (I) initer;
    }

    StartIniter(Class<?> initerClass, InitLevel initLevel) {
        this.initerClass = initerClass;
        this.initLevel = initLevel;
    }

    public Class<?> getIniterClass() {
        return initerClass;
    }

    public InitLevel getInitLevel() {
        return initLevel;
    }

    public P getNext() {
        return next;
    }

    public P getPrev() {
        return prev;
    }

    public P head() {
        if (this.prev == null)
            return (P) this;
        else
            return (P) prev.head();
    }

    public P append(P initer) {
        if (next != null)
            throw new IllegalArgumentException(LogUtils.format("{} next is exist {}", this, this.next));
        initer.setPrev(this);
        return next = initer;
    }


    void setPrev(P initer) {
        if (prev != null)
            throw new IllegalArgumentException(LogUtils.format("{} prev is exist {}", this, this.prev));
        this.prev = initer;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "initerClass=" + initerClass +
                '}';
    }
}
