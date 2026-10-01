/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

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

    /**
     * 全部静态 @StaticInit 方法（按方法名确定序）。
     * 原实现只保留遍历序"最后一个"，同类多方法时其余静默不执行且结果非确定。
     */
    private final Method[] methods;

    private final AsLifecycle lifecycle;

    private StaticInitiator(Class<?> InitiatorClass, AsLifecycle lifecycle, Method[] methods) {
        this.InitiatorClass = InitiatorClass;
        this.lifecycle = lifecycle;
        this.methods = methods;
    }

    public Class<?> getInitiatorClass() {
        return this.InitiatorClass;
    }

    public void init() throws Exception {
        for (Method method : this.methods) {
            try {
                method.invoke(null);
            } catch (InvocationTargetException e) {
                // 解包真实异常：调用方（boot 引擎）需要看到业务异常而非反射包装
                Throwable target = e.getTargetException();
                if (target instanceof Exception exception) {
                    throw exception;
                }
                if (target instanceof Error error) {
                    throw error;
                }
                throw e;
            }
        }
    }

    public static StaticInitiator instance(Class<?> clazz) {
        AsLifecycle lifecycle = clazz.getAnnotation(AsLifecycle.class);
        if (lifecycle == null) {
            throw new NullPointerException(format("{} 没有 {} 注解", clazz, AsLifecycle.class));
        }
        java.util.List<Method> candidates = new java.util.ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            int modifiers = method.getModifiers();
            if (Modifier.isStatic(modifiers) && method.getAnnotation(StaticInit.class) != null) {
                candidates.add(method);
            }
        }
        if (candidates.isEmpty()) {
            throw new IllegalArgumentException(format("{} 不存在 {} 方法", clazz, StaticInit.class));
        }
        candidates.sort(java.util.Comparator.comparing(Method::getName));
        Method[] methods = candidates.toArray(new Method[0]);
        for (Method method : methods) {
            method.setAccessible(true);
        }
        return new StaticInitiator(clazz, lifecycle, methods);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof StaticInitiator)) {
            return false;
        }

        StaticInitiator that = (StaticInitiator) o;

        return new EqualsBuilder().append(getInitiatorClass(), that.getInitiatorClass())
                .append(java.util.Arrays.toString(methods), java.util.Arrays.toString(that.methods))
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getInitiatorClass())
                .append(java.util.Arrays.toString(methods)).toHashCode();
    }

    @Override
    public int compareTo(StaticInitiator other) {
        int value = other.lifecycle.order() - this.lifecycle.order();
        if (value != 0) {
            return value;
        }
        int classCompare = this.InitiatorClass.getName().compareTo(other.InitiatorClass.getName());
        if (classCompare != 0) {
            return classCompare;
        }
        // 原 compareTo 不含方法信息：同类两条记录在 ConcurrentSkipListSet 中按 compareTo==0 静默去重，
        // 与 equals（含 method）矛盾
        return java.util.Arrays.toString(this.methods).compareTo(java.util.Arrays.toString(other.methods));
    }

}
