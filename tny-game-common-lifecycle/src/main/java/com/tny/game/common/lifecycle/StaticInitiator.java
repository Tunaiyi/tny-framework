package com.tny.game.common.lifecycle;

import com.tny.game.common.lifecycle.annotation.*;
import org.apache.commons.lang3.builder.*;

import java.lang.reflect.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * Created by Kun Yang on 2016/12/16.
 */
public class StaticInitiator implements Comparable<StaticInitiator> {

    private final Class<?> InitiatorClass;

    private final Method method;

    private final AsLifecycle lifecycle;

    private StaticInitiator(Class<?> InitiatorClass, AsLifecycle lifecycle, Method method) {
        this.InitiatorClass = InitiatorClass;
        this.lifecycle = lifecycle;
        this.method = method;
    }

    public Class<?> getInitiatorClass() {
        return this.InitiatorClass;
    }

    public void init() throws Exception {
        this.method.invoke(null);
    }

    public static StaticInitiator instance(Class<?> clazz) {
        AsLifecycle lifecycle = clazz.getAnnotation(AsLifecycle.class);
        if (lifecycle == null) {
            throw new NullPointerException(format("{} 没有 {} 注解", clazz, AsLifecycle.class));
        }
        Method lifecycleMethod = null;
        for (Method method : clazz.getDeclaredMethods()) {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) && method.getAnnotation(StaticInit.class) != null) {
                lifecycleMethod = method;
            }
            method.setAccessible(true);
        }
        if (lifecycleMethod == null) {
            throw new IllegalArgumentException(format("{} 不存在 {} 方法", clazz, StaticInit.class));
        }
        return new StaticInitiator(clazz, lifecycle, lifecycleMethod);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof StaticInitiator)) {
            return false;
        }

        StaticInitiator that = (StaticInitiator)o;

        return new EqualsBuilder().append(getInitiatorClass(), that.getInitiatorClass())
                .append(method, that.method)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getInitiatorClass()).append(method).toHashCode();
    }

    @Override
    public int compareTo(StaticInitiator other) {
        int value = other.lifecycle.order() - this.lifecycle.order();
        if (value != 0) {
            return value;
        }
        return this.InitiatorClass.getName().compareTo(other.InitiatorClass.getName());
    }

}
