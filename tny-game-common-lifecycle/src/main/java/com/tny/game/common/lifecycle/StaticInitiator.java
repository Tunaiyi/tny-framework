package com.tny.game.common.lifecycle;

import com.tny.game.common.lifecycle.annotaion.*;

import java.lang.reflect.*;

import static com.tny.game.common.utils.StringAide.*;

/**
 * Created by Kun Yang on 2016/12/16.
 */
public class StaticInitiator {

    private Class<?> InitiatorClass;
    private Method method;

    private StaticInitiator(Class<?> InitiatorClass, Method method) {
        this.InitiatorClass = InitiatorClass;
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
        if (lifecycle == null)
            throw new NullPointerException(format("{} 没有 {} 注解", clazz, AsLifecycle.class));
        Method lifecycleMethod = null;
        for (Method method : clazz.getDeclaredMethods()) {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) && method.getAnnotation(StaticInit.class) != null)
                lifecycleMethod = method;
            method.setAccessible(true);
        }
        if (lifecycleMethod == null)
            throw new IllegalArgumentException(format("{} 不存在 {} 方法", clazz, StaticInit.class));
        return new StaticInitiator(clazz, lifecycleMethod);
    }

}
