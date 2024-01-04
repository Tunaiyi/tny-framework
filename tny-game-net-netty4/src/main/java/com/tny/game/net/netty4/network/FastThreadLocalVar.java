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
