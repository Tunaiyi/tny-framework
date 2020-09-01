package com.tny.game.suite.auto;

import com.tny.game.common.concurrent.collection.*;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Kun Yang on 16/1/28.
 */
public class AutoMethodHolder<M extends AutoMethod<?, ?, ?>> {

    private final Map<Method, M> METHOD_MAP = new CopyOnWriteMap<>();

    private Function<Method, M> creator;

    public AutoMethodHolder(Function<Method, M> creator) {
        this.creator = creator;
    }

    public AutoMethodHolder() {
    }

    public M getInstance(Method method) {
        return getInstance(method, this.creator);
    }

    public M getInstance(Method method, Function<Method, M> creator) {
        M autoMethod = this.METHOD_MAP.get(method);
        if (autoMethod != null) {
            return autoMethod;
        }
        synchronized (method) {
            autoMethod = this.METHOD_MAP.get(method);
            if (autoMethod != null) {
                return autoMethod;
            }
            autoMethod = creator.apply(method);
            this.METHOD_MAP.put(method, autoMethod);
            return autoMethod;
        }
    }

}
