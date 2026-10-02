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
        forEachLoader(unit, loader -> loader.put(name, unit));
    }

    /**
     * 遍历该 unit 应注册的全部 loader（@UnitInterface 继承链 + @Unit.unitInterfaces）。
     */
    private static void forEachLoader(Object unit, java.util.function.Consumer<UnitLoader<Object>> action) {
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
            action.accept(loader);
        }
        Unit unitAnnotation = unitClass.getAnnotation(Unit.class);
        if (unitAnnotation != null) {
            for (Class<?> unitInterface : unitAnnotation.unitInterfaces()) {
                if (!registerInterfaces.add(unitInterface)) {
                    continue;
                }
                UnitLoader<Object> loader = as(getLoader(unitInterface));
                action.accept(loader);
            }
        }
        Asserts.checkArgument(!registerInterfaces.isEmpty(), "register {} unit, but unit is not instance of UnitInterface", unit);
    }

    /**
     * 写入前的整体验证（类型 + 同名冲突）。原实现边写边校验：中途失败时前面的名字/接口
     * 已落入全局 unitLoaders，留下半注册状态且重试必炸"same name"。
     */
    private void validate(String name, Object unit) {
        Asserts.checkInstanceOf(unit, this.unitInterface, "UnitLoader [{}] loading unit, Unit {} is not instance of {}",
                this.unitInterface, unit, this.unitInterface);
        Asserts.checkArgument(!this.unitMap.containsKey(name),
                "UnitLoader [{}] loading unit, Unit {} have the same name {}", this.unitInterface, unit, name);
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
        // 先整体预校验再逐名写入（消除部分注册无回滚问题）
        for (String name : names) {
            forEachLoader(unit, loader -> loader.validate(name, unit));
        }
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
