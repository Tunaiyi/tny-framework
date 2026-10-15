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
 * 标记自定义ProtoEx类型字段
 * <p>
 * 若字段为Repeat(Collection)类型时还需要标记 @ProtoExElement
 * 若字段为Map类型时还需要标记 @ProtoExEntry
 *
 * @author KGTny
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ProtoExField {

    /**
     * ProtoEx类型字段ID 取值方位 1 - 536870911
     *
     * @return
     */
    int value();

    /**
     * 配置字段编码方式
     *
     * @return
     */
    ProtoExConf conf() default @ProtoExConf;

}
