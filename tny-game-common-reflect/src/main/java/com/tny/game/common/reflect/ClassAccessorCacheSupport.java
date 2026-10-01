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

package com.tny.game.common.reflect;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiFunction;

/**
 * {@code CGlibUtils} / {@code JavassistAccessors} 的共享骨架（reduce-code-duplication 组5）。
 * 两面 getGClass 双检缓存逐字同构，唯一差异是实例化目标（CGlibClassAccessor vs JSsistClassAccessor）——
 * 以工厂形参承载（D4"单一事实源+两侧现状双保"先例）。每面持有独立实例（原各自 static 双表语义保留：
 * 两面缓存互不复用）。判定细节逐字保留：{@code synchronized (clazz)} 锁对象、
 * {@code putIfAbsent} 竞态返回先注册者、null clazz 先查表后在同步处 NPE 的路径顺序。
 * 内部件，方法名不映射任何 public 契约。
 */
public final class ClassAccessorCacheSupport {

    /** 缓存键=（目标类，过滤器引用身份）：原仅按类键控，带/不带过滤器的调用方互相吞掉方法集 */
    private final ConcurrentMap<CacheKey, ClassAccessor> classMap = new ConcurrentHashMap<>();

    public ClassAccessor get(Class<?> clazz, MethodFilter filter,
                             BiFunction<Class<?>, MethodFilter, ClassAccessor> accessorFactory) {
        CacheKey key = new CacheKey(clazz, filter);
        ClassAccessor gClass = this.classMap.get(key);
        if (gClass != null) {
            return gClass;
        }
        synchronized (clazz) {
            gClass = this.classMap.get(key);
            if (gClass != null) {
                return gClass;
            }
            gClass = accessorFactory.apply(clazz, filter);
            ClassAccessor oldClass = this.classMap.putIfAbsent(key, gClass);
            return oldClass == null ? gClass : oldClass;
        }
    }

    static final class CacheKey {

        private final Class<?> clazz;

        private final MethodFilter filter;

        CacheKey(Class<?> clazz, MethodFilter filter) {
            this.clazz = clazz;
            this.filter = filter;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CacheKey)) {
                return false;
            }
            CacheKey that = (CacheKey) o;
            return clazz == that.clazz && filter == that.filter;
        }

        @Override
        public int hashCode() {
            return Objects.hash(clazz, System.identityHashCode(filter));
        }
    }

}
