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
public class EnumeratorHolder<O> implements Enumerator<O> {

    private final Map<Object, O> enumeratorMap;

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
        if (object instanceof EnumIdentifiable) {
            putAndCheck(((EnumIdentifiable<?>)object).getId(), object);
        }
        postRegister(object);
    }

    protected void postRegister(O object) {
    }

    protected EnumeratorHolder<O> putAndCheck(Object key, O value) {
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
        return this;
    }

    @Override
    public <T extends O> T check(Object key, String message, Object... args) {
        T value = (T)this.enumeratorMap.get(key);
        if (value == null) {
            throw new NullPointerException(StringAide.format(message, args));
        }
        return value;
    }

    @Override
    public <T extends O> T of(Object key) {
        return (T)this.enumeratorMap.get(key);
    }

    @Override
    public <T extends O> Optional<T> option(Object key) {
        return Optional.ofNullable((T)this.enumeratorMap.get(key));
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