package com.tny.game.common.unit;

import com.tny.game.common.collection.*;
import com.tny.game.common.reflect.*;
import com.tny.game.common.unit.annotation.*;
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

    private static ConcurrentMap<Class<?>, UnitLoader<?>> unitLoaders = new ConcurrentHashMap<>();

    public static final Logger LOGGER = LoggerFactory.getLogger(UnitLoader.class);

    private Class<T> unitInterface;

    private ConcurrentMap<String, T> unitMap = new ConcurrentHashMap<>();

    private Set<T> unitSet = new ConcurrentHashSet<>();

    private UnitLoader(Class<T> unitInterface) {
        this.unitInterface = unitInterface;
    }

    public static void register(String name, Object unit) {
        Class<?> unitClass = unit.getClass();
        Set<Class<?>> unitClasses = ReflectAide.getDeepClasses(unitClass);
        Set<Class<?>> registerInterfaces = new HashSet<>();
        for (Class<?> clazz : unitClasses) {
            UnitInterface unitInterface = clazz.getAnnotation(UnitInterface.class);
            if (unitInterface == null)
                continue;
            if (!registerInterfaces.add(clazz))
                continue;
            UnitLoader<Object> loader = as(getLoader(clazz));
            loader.put(name, unit);
        }
        Unit unitAnnotation = unitClass.getAnnotation(Unit.class);
        if (unitAnnotation != null) {
            for (Class<?> unitInterface : unitAnnotation.unitInterfaces()) {
                if (!registerInterfaces.add(unitInterface))
                    continue;
                UnitLoader<Object> loader = as(getLoader(unitInterface));
                loader.put(name, unit);
            }
        }
        Throws.checkArgument(!registerInterfaces.isEmpty(), "register {} unit, but unit is not instance of UnitInterface", unit);
    }

    public static Set<String> register(Object unit) {
        Class<?> unitClass = unit.getClass();
        Unit unitAnnotation = unitClass.getAnnotation(Unit.class);
        Throws.checkNotNull(unitAnnotation, "register {} unit, but unit is without {} Annotation", unit, Unit.class);
        Set<String> names = new HashSet<>();
        if (StringUtils.isNoneBlank(unitAnnotation.value()))
            names.add(unitAnnotation.value());
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
        Throws.checkInstanceOf(unit, unitInterface, "UnitLoader [{}] loading unit, Unit {} is not instance of {}",
                unitInterface, unit, unitInterface);
        T old = this.unitMap.putIfAbsent(name, unit);
        Throws.checkArgument(old == null, "UnitLoader [{}] loading unit, Unit {} and Unit {} have the same name {}", unitInterface, unit, old, name);
        this.unitSet.add(unit);
    }

    public T getOneUnitAnCheck() {
        T unit = getOne().orElse(null);
        Throws.checkNotNull(unit, "UnitLoader [{}] is not exist unit", unitInterface, 0);
        return unit;
    }

    public Optional<T> getOne() {
        return unitSet.stream().findFirst();
    }

    public Collection<T> getAllUnits() {
        return Collections.unmodifiableCollection(unitSet);
    }

    public <O extends T> O getUnit(String name, O defaultUnit) {
        return as(unitMap.getOrDefault(name, defaultUnit));
    }

    public Optional<T> getUnit(String name) {
        return Optional.ofNullable(unitMap.get(name));
    }

    public <O extends T> O getUnitAnCheck(String name) {
        return as(Throws.checkNotNull(unitMap.get(name), "UnitLoader [{}] is not exist unit {}", unitInterface, name));
    }

    public static <T> UnitLoader<T> getLoader(Class<T> unitInterface) {
        return as(unitLoaders.computeIfAbsent(unitInterface, UnitLoader::new));
    }

}
