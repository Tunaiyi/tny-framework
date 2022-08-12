/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.lifecycle.unit;

import com.tny.game.common.concurrent.collection.*;
import com.tny.game.common.lifecycle.unit.annotation.*;
import com.tny.game.common.reflect.*;
import com.tny.game.common.utils.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;

import java.util.*;
import java.util.concurrent.*;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 */
public class UnitLoader<T> {

    private static final ConcurrentMap<Class<?>, UnitLoader<?>> unitLoaders = new ConcurrentHashMap<>();

    public static final Logger LOGGER = LoggerFactory.getLogger(UnitLoader.class);

    private final Class<T> unitInterface;

    private final ConcurrentMap<String, T> unitMap = new ConcurrentHashMap<>();

    private final Set<T> unitSet = new ConcurrentHashSet<>();

    private UnitLoader(Class<T> unitInterface) {
        this.unitInterface = unitInterface;
    }

    public static void register(String name, Object unit) {
        Class<?> unitClass = unit.getClass();
        Set<Class<?>> unitClasses = ReflectAide.getDeepClasses(unitClass);
        Set<Class<?>> registerInterfaces = new HashSet<>();
        for (Class<?> clazz : unitClasses) {
            UnitInterface unitInterface = clazz.getAnnotation(UnitInterface.class);
            if (unitInterface == null) {
                continue;
            }
            if (!registerInterfaces.add(clazz)) {
                continue;
            }
            UnitLoader<Object> loader = as(getLoader(clazz));
            loader.put(name, unit);
        }
        Unit unitAnnotation = unitClass.getAnnotation(Unit.class);
        if (unitAnnotation != null) {
            for (Class<?> unitInterface : unitAnnotation.unitInterfaces()) {
                if (!registerInterfaces.add(unitInterface)) {
                    continue;
                }
                UnitLoader<Object> loader = as(getLoader(unitInterface));
                loader.put(name, unit);
            }
        }
        Asserts.checkArgument(!registerInterfaces.isEmpty(), "register {} unit, but unit is not instance of UnitInterface", unit);
    }

    public static Set<String> register(Object unit) {
        Class<?> unitClass = unit.getClass();
        Unit unitAnnotation = unitClass.getAnnotation(Unit.class);
        Asserts.checkNotNull(unitAnnotation, "register {} unit, but unit is without {} Annotation", unit, Unit.class);
        Set<String> names = new HashSet<>();
        if (StringUtils.isNoneBlank(unitAnnotation.value())) {
            names.add(unitAnnotation.value());
        }
        names.add(unitClass.getSimpleName());
        names.add(unitClass.getName());
        for (String name : names)
            register(name, unit);
        return names;
    }

    public static void register(Collection<?> units) {
        for (Object object : units)
            register(object);
    }

    private void put(String name, T unit) {
        Asserts.checkInstanceOf(unit, this.unitInterface, "UnitLoader [{}] loading unit, Unit {} is not instance of {}",
                this.unitInterface, unit, this.unitInterface);
        T old = this.unitMap.putIfAbsent(name, unit);
        Asserts.checkArgument(old == null, "UnitLoader [{}] loading unit, Unit {} and Unit {} have the same name {}", this.unitInterface, unit, old,
                name);
        this.unitSet.add(unit);
    }

    public T checkUnit() {
        T unit = getUnit().orElse(null);
        Asserts.checkNotNull(unit, "UnitLoader [{}] is not exist unit", this.unitInterface, 0);
        return unit;
    }

    public Optional<T> getUnit() {
        return this.unitSet.stream().findFirst();
    }

    public <E extends T> Collection<E> getAllUnits() {
        return as(Collections.unmodifiableCollection(this.unitSet));
    }

    public <O extends T> O getUnit(String name, O defaultUnit) {
        return as(this.unitMap.getOrDefault(name, defaultUnit));
    }

    public Optional<T> getUnit(String name) {
        return Optional.ofNullable(this.unitMap.get(name));
    }

    public <O extends T> O checkUnit(String name) {
        return as(Asserts.checkNotNull(this.unitMap.get(name), "UnitLoader [{}] is not exist unit {}", this.unitInterface, name));
    }

    public static <T> UnitLoader<T> getLoader(Class<T> unitInterface) {
        return as(unitLoaders.computeIfAbsent(unitInterface, UnitLoader::new));
    }

}
