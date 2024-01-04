package com.tny.game.suite.auto;

import com.google.common.collect.ImmutableMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public abstract class AutoMethod<I extends Annotation, R extends Annotation, P extends Annotation> {

    protected I autoInvoke;

    protected R autoReturn;

    protected Map<Integer, P> autoParamMap;

    protected AutoMethod(Method method, Class<I> iClass, Class<R> rClass, Class<P> pClass) {
        this.initAutoSave(method, iClass, rClass, pClass);
    }

    @SuppressWarnings("unchecked")
    protected void initAutoSave(Method method, Class<I> iClass, Class<R> rClass, Class<P> pClass) {
        if (iClass != null) {
            this.autoInvoke = method.getAnnotation(iClass);
        }
        if (rClass != null) {
            this.autoReturn = method.getAnnotation(rClass);
        }
        if (pClass != null) {
            Annotation[][] annotationsArray = method.getParameterAnnotations();
            this.autoParamMap = null;
            int index = 0;
            for (Annotation[] annotations : annotationsArray) {
                for (Annotation ann : annotations) {
                    if (pClass.isInstance(ann)) {
                        if (autoParamMap == null) {
                            autoParamMap = new HashMap<>();
                        }
                        this.autoParamMap.put(index, (P) ann);
                    }
                }
                index++;
            }
            if (this.autoParamMap == null) {
                this.autoParamMap = ImmutableMap.of();
            } else {
                this.autoParamMap = ImmutableMap.copyOf(autoParamMap);
            }
        }
    }

    public boolean isAutoHandle() {
        return this.autoInvoke != null || this.autoReturn != null || !this.autoParamMap.isEmpty();
    }

    public boolean isHandleInvoke() {
        return this.autoInvoke != null;
    }

    public boolean isHandleReturn() {
        return this.autoReturn != null;
    }

    public boolean isHandleParams() {
        return !this.autoParamMap.isEmpty();
    }

    public I getAutoInvoke() {
        return this.autoInvoke;
    }

    public R getAutoReturn() {
        return this.autoReturn;
    }

    public P getAutoSaveParam(int index) {
        return this.autoParamMap.get(index);
    }

}
