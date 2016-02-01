package com.tny.game.suite.auto;

import com.tny.game.common.utils.collection.CopyOnWriteMap;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kun Yang on 16/1/28.
 */
public class AutoMethodHolder<M extends AutoMethod> {

    private final Map<Method, M> METHOD_MAP = new CopyOnWriteMap<>();

    private Function<Method, M> creator;

    public AutoMethodHolder(Function<Method, M> creator) {
        this.creator = creator;
    }

    public M getInstance(Method method) {
        M autoMethod = METHOD_MAP.get(method);
        if (autoMethod != null)
            return autoMethod;
        synchronized (method) {
            autoMethod = METHOD_MAP.get(method);
            if (autoMethod != null)
                return autoMethod;
            autoMethod = creator.apply(method);
            METHOD_MAP.put(method, autoMethod);
            return autoMethod;
        }
    }

}
