/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.enums;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.utils.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * Created by Kun Yang on 16/1/29.
 */
@SuppressWarnings("unchecked")
public class EnumeratorHolder<O extends Enumerable<?>> implements Enumerator<O> {

    private final Map<Object, O> enumeratorMap;

    private final Map<EnumerableSymbol<O, ?>, Map<Object, O>> symbolEnumeratorMap = new CopyOnWriteMap<>();

    private final Set<O> enumerators;

    private final Set<Class<? extends O>> classes;

    public EnumeratorHolder() {
        this(new CopyOnWriteMap<>(), new CopyOnWriteArraySet<>(), new CopyOnWriteArraySet<>());
    }

    public EnumeratorHolder(Map<Object, O> enumeratorMap, Set<O> enumerators, Set<Class<? extends O>> classes) {
        this.enumeratorMap = enumeratorMap;
        this.enumerators = enumerators;
        this.classes = classes;
    }

    public void register(O... object) {
        for (O o : object)
            register(o);
    }

    public void register(Iterable<O> object) {
        for (O o : object)
            register(o);
    }

    public void register(O object) {
        if (object.getClass().isEnum()) {
            putAndCheck(((Enum<?>)object).name(), object);
        }
        putAndCheck(((Enumerable<?>)object).getId(), object);
        postRegister(object);
    }

    protected void postRegister(O object) {
    }

    private Map<Object, O> symbolMap(EnumerableSymbol<O, ?> symbol) {
        return symbolEnumeratorMap.computeIfAbsent(symbol, (s) -> new CopyOnWriteMap<>());
    }

    protected <T> EnumeratorHolder<O> putAndCheckSymbol(EnumerableSymbol<O, T> symbol, O value) {
        T symbolValue = symbol.getSymbolValue(value);
        Map<Object, O> symbolMap = symbolMap(symbol);
        O old = symbolMap.putIfAbsent(symbolValue, value);
        if (old != null && old != value) {
            throw new IllegalStateException(StringAide.format(
                    "注册 {} [symbol[{}]:{}, object:{}] 发现已存在对象 {}", value.getClass(), symbol, symbolValue, value, old));
        }
        return this;
    }

    private void putAndCheck(Object key, O value) {
        O old = this.enumeratorMap.putIfAbsent(key, value);
        if (old != null) {
            if (old != value) {
                throw new IllegalStateException(StringAide.format(
                        "注册 {} [key:{}, object:{}] 发现已存在对象 {}", value.getClass(), key, value, old));
            }
        } else {
            this.enumerators.add(value);
            this.classes.add((Class<? extends O>)value.getClass());
        }
    }

    @Override
    public <T extends O> T check(Object key, String message, Object... args) {
        T value = (T)this.enumeratorMap.get(key);
        if (value == null) {
            throw new NullPointerException(StringAide.format(message, args));
        }
        return value;
    }

    public <T extends O, S> T checkBySymbol(EnumerableSymbol<O, S> symbol, S key, String message, Object... args) {
        T value = (T)this.symbolMap(symbol).get(key);
        if (value == null) {
            throw new NullPointerException(StringAide.format(message, args));
        }
        return value;
    }

    @Override
    public <T extends O> T of(Object key) {
        return (T)this.enumeratorMap.get(key);
    }

    public <T extends O, S> T ofBySymbol(EnumerableSymbol<O, S> symbol, S key) {
        return (T)this.symbolMap(symbol).get(key);
    }

    @Override
    public <T extends O> Optional<T> option(Object key) {
        return Optional.ofNullable((T)this.enumeratorMap.get(key));
    }

    public <T extends O, S> Optional<T> optionBySymbol(EnumerableSymbol<O, S> symbol, S key) {
        return Optional.ofNullable((T)this.symbolMap(symbol).get(key));
    }

    @Override
    public <T extends O> Set<T> allValues() {
        return (Set<T>)Collections.unmodifiableSet(this.enumerators);
    }

    @Override
    public Set<Class<? extends O>> allClasses() {
        return Collections.unmodifiableSet(this.classes);
    }

    @Override
    public <T extends Enum<T>> Set<Class<T>> allEnumClasses() {
        return this.classes.stream().filter(Class::isEnum).map((c) -> (Class<T>)c).collect(Collectors.toSet());
    }

}
