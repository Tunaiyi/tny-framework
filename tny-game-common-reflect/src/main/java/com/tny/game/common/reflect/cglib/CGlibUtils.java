/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.common.reflect.cglib;

import com.tny.game.common.reflect.*;

/**
 * cglib 面类访问器工厂——双检缓存骨架收敛至 {@link ClassAccessorCacheSupport}（组5），
 * 实例化目标以本包 {@code CGlibClassAccessor} 构造器引用作工厂实参；public 工厂签名逐字冻结。
 * 本面独立缓存实例（原 static 双表语义保留，两面互不复用）。
 */
public class CGlibUtils {

    private final static ClassAccessorCacheSupport CACHE = new ClassAccessorCacheSupport();

    public static ClassAccessor getGClass(Class<?> clazz) {
        return CACHE.get(clazz, null, CGlibClassAccessor::new);
    }

    public static ClassAccessor getGClass(Class<?> clazz, MethodFilter filter) {
        return CACHE.get(clazz, filter, CGlibClassAccessor::new);
    }

}
