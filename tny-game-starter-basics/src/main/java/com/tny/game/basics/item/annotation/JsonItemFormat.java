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

package com.tny.game.basics.item.annotation;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.mapper.*;

import java.lang.annotation.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/12/3 2:30 上午
 */

@Inherited
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonProperty
@JsonDeserialize(using = ItemJsonDeserializer.class)
@JsonSerialize(using = ItemJsonSerializer.class)
@Documented
public @interface JsonItemFormat {

    Class<? extends Manager<?>> value();

}
