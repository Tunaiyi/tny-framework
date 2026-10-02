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

package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tny.game.common.result.*;

import java.time.Instant;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 3:22 下午
 */
public class ExtensionModule extends SimpleModule {

    public ExtensionModule() {
        extensionModule(Instant.class, InstantJsonSerializer.getDefault(), InstantJsonDeserializer.getDefault());
        extensionModule(ResultCode.class, ResultCodeJsonSerializer.getDefault(), ResultCodeJsonDeserializer.getDefault());
    }

    public <T> void extensionModule(Class<T> clazz, JsonSerializer<T> serializer, JsonDeserializer<T> deserializer) {
        this.addSerializer(clazz, serializer);
        this.addDeserializer(clazz, deserializer);
    }

}
