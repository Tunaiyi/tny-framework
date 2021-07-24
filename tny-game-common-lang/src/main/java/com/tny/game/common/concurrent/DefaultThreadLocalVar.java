package com.tny.game.common.concurrent;

import java.util.function.Supplier;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/24 10:36 上午
 */
public class DefaultThreadLocalVar<T> implements ThreadLocalVar<T> {

    private final ThreadLocal<T> threadLocal;

    public DefaultThreadLocalVar() {
        this.threadLocal = new ThreadLocal<>();
    }

    public DefaultThreadLocalVar(Supplier<T> supplier) {
        this.threadLocal = ThreadLocal.withInitial(supplier);
    }

    @Override
    public T get() {
        return this.threadLocal.get();
    }

    @Override
    public void set(T value) {
        this.threadLocal.set(value);
    }

    @Override
    public void remove() {
        this.threadLocal.remove();
    }

}
