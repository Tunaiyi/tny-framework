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

package com.tny.game.common.reflect.javassist;

import com.tny.game.common.reflect.*;

/**
 * javassist 面类访问器工厂——双检缓存骨架收敛至 {@link ClassAccessorCacheSupport}（组5），
 * 实例化目标以本包 {@code JSsistClassAccessor} 构造器引用作工厂实参；public 工厂签名逐字冻结。
 * 本面独立缓存实例（原 static 双表语义保留，两面互不复用）；跨模块消费形态（protoex/net/data 静态调用）不动。
 */
public class JavassistAccessors {

    private final static ClassAccessorCacheSupport CACHE = new ClassAccessorCacheSupport();

    public static ClassAccessor getGClass(Class<?> clazz) {
        return CACHE.get(clazz, null, JSsistClassAccessor::new);
    }

    public static ClassAccessor getGClass(Class<?> clazz, MethodFilter filter) {
        return CACHE.get(clazz, filter, JSsistClassAccessor::new);
    }

}
