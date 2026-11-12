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
package com.tny.game.cache.annotation;

import com.tny.game.cache.*;

import java.lang.annotation.*;

/**
 * 调用Cache中 **Object(...) 方法的对象必须有该注解 Cache会根据该注解构建key，用于存储删除对象
 *
 * @author KGTny
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ToCache {

    /**
     * 关联key前缀
     * <p>
     * <p>
     * 关联Item的key的前缀<br>
     *
     * @return 返回key前缀
     */
    String prefix() default "";

    /**
     * 关联对象的处理器类型
     *
     * @return 处理器类型
     */
    Class<? extends CacheTrigger<?, ?, ?>>[] triggers() default {};

    /**
     * 构成key的方法名称数组 如: key = getId() + "_" + getName();
     * <p>
     * cacheKeys = {"id", "name"}
     *
     * @return
     */
    String[] cacheKeys();

    /**
     * 数据源
     *
     * @return
     */
    String source() default "";

    /**
     * 起效描述
     *
     * @return
     */
    String[] profiles() default {};

}
