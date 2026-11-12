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

import java.lang.annotation.*;

/**
 * ProtoEx Map字段中Key和Value编码方式
 *
 * @author KGTny
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ProtoExEntry {

    /**
     * key编码方式配置 默认为显式发送ProtoExID
     *
     * @return
     */
    ProtoExConf key() default @ProtoExConf(typeEncode = TypeEncode.EXPLICIT);

    /**
     * value编码方式配置 默认为显式发送ProtoExID
     *
     * @return
     */
    ProtoExConf value() default @ProtoExConf(typeEncode = TypeEncode.EXPLICIT);

}
