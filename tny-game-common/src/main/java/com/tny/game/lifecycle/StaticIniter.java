package com.tny.game.lifecycle;

import com.tny.game.LogUtils;
import com.tny.game.lifecycle.annotaion.AsLifecycle;
import com.tny.game.lifecycle.annotaion.StaticInit;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by Kun Yang on 2016/12/16.
 */
public class StaticIniter {

    private Method method;

    private StaticIniter(Method method) {
        this.method = method;
    }

    public void init() throws Throwable {
        method.invoke(null);
    }

    public static StaticIniter instance(Class<?> clazz) {
        AsLifecycle lifecycle = clazz.getAnnotation(AsLifecycle.class);
        if (lifecycle == null)
            throw new NullPointerException(LogUtils.format("{} 没有 {} 注解", clazz, AsLifecycle.class));
        Method lifecycleMethod = null;
        for (Method method : clazz.getDeclaredMethods()) {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) && method.getAnnotation(StaticInit.class) != null)
                lifecycleMethod = method;
            method.setAccessible(true);
        }
        if (lifecycleMethod == null)
            throw new IllegalArgumentException(LogUtils.format("{} 不存在 {} 方法", clazz, StaticInit.class));
        return new StaticIniter(lifecycleMethod);
    }

}
