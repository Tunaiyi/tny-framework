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

package com.tny.game.basics.item.loader.jackson;

import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/17 04:43
 **/
public class EnumKeyDeserializer<T> extends KeyDeserializer {

    private final EnumMapper<T> enumMapper;

    public EnumKeyDeserializer(EnumMapper<T> enumMapper) {
        this.enumMapper = enumMapper;
    }

    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        Object enumObject = this.enumMapper.getEnum(key);
        if (enumObject == null) {
            throw new NullPointerException(MessageFormat.format("无法找到{0}枚举类型", key));
        }
        return enumObject;
    }

}
