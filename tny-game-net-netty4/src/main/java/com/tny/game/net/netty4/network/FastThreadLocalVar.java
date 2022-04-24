package com.tny.game.net.netty4.network;

import com.tny.game.common.concurrent.*;
import io.netty.util.concurrent.FastThreadLocal;

import java.util.function.Supplier;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/24 10:40 上午
 */
public class FastThreadLocalVar<T> implements ThreadLocalVar<T> {

    private final FastThreadLocal<T> threadLocal;

    private Supplier<T> supplier = null;

    public FastThreadLocalVar() {
        this.threadLocal = new FastThreadLocal<>();
    }

    public FastThreadLocalVar(Supplier<T> supplier) {
        this();
        this.supplier = supplier;
    }

    public FastThreadLocalVar(FastThreadLocal<T> threadLocal) {
        this.threadLocal = threadLocal;
    }

    @Override
    public T get() {
        T value = this.threadLocal.get();
        if (value == null && this.supplier != null) {
            value = this.supplier.get();
            if (value != null) {
                this.threadLocal.set(value);
            }
        }
        return value;
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
