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

package com.tny.game.protoex.annotations;

import com.tny.game.protoex.field.*;

import java.lang.annotation.*;

/**
 * ProtoEx字段编码配置
 *
 * @author KGTny
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ProtoExConf {

    /**
     * int long short 字段的编码方式
     * 默认为 FieldFormat.DEFAULT
     *
     * @return
     */
    FieldFormat format() default FieldFormat.DEFAULT;

    /**
     * 字段类型的ProtoExID编码方式 默认为TypeEncode.DEFAULT
     *
     * @return
     */
    TypeEncode typeEncode() default TypeEncode.DEFAULT;

    /**
     * 使用指定类型
     *
     * @return
     */
    Class<?> use() default Void.class;

}
