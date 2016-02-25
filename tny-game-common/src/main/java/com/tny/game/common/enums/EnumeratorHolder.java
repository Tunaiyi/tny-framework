package com.tny.game.common.enums;

import com.tny.game.LogUtils;
import com.tny.game.common.utils.collection.CopyOnWriteMap;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

/**
 * Created by Kun Yang on 16/1/29.
 */
@SuppressWarnings("unchecked")
public class EnumeratorHolder<O> {

    protected Map<Object, O> enumeratorMap;

    protected Set<O> enumerators;

    protected Set<Class<? extends O>> classes;

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
            putAndCheck(((Enum<?>) object).name(), object);
        }
        if (object instanceof EnumID) {
            putAndCheck(((EnumID) object).getID(), object);
        }
        postRegister(object);
    }

    protected void postRegister(O object) {
    }

    protected EnumeratorHolder<O> putAndCheck(Object key, O o) {
        if (enumeratorMap.containsKey(key)) {
            O old = enumeratorMap.get(key);
            if (old != null)
                throw new IllegalStateException(LogUtils.format(
                        "注册 {} [key:{}, object:{}] 发现已存在对象 {}", o.getClass(), key, o, old));
        }
        enumeratorMap.put(key, o);
        enumerators.add(o);
        classes.add((Class<? extends O>) o.getClass());
        return this;
    }


    public <T extends O> T ofAndCheck(Object key, String message, Object... args) {
        T value = (T) enumeratorMap.get(key);
        if (value == null)
            throw new NullPointerException(LogUtils.format(message, args));
        return value;
    }

    public <T extends O> T of(Object key) {
        return (T) enumeratorMap.get(key);
    }

    public Set<O> values() {
        return Collections.unmodifiableSet(this.enumerators);
    }

    public Set<Class<? extends O>> getAllClasses() {
        return Collections.unmodifiableSet(this.classes);
    }

    public Set<Class<? extends Enum>> getAllEnumClasses() {
        return this.classes.stream().filter(Class::isEnum).map((c) -> (Class<Enum>) c).collect(Collectors.toSet());
    }

}
