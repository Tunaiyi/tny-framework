/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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
