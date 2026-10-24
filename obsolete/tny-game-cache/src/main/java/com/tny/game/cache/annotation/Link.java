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

import com.tny.game.asyndb.*;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Deprecated
public @interface Link {

    /**
     * 重新定义项的名称，默认为字段名
     *
     * @return
     */
    public String name() default "";

    /**
     * 是否操作拥有者的时候忽略更新所属项
     *
     * @return
     */
    public boolean ignore() default false;

    /**
     * 当ignore为true并且拥有者操作以下操作的时候忽略更新所属项
     *
     * @return
     */
    public Operation[] ignoreOperation() default {Operation.INSERT, Operation.DELETE, Operation.SAVE, Operation.UPDATE};

}
