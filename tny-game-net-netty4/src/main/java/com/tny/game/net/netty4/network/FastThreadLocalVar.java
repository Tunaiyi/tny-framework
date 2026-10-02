/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
