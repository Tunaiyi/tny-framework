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

package com.tny.game.data.mongodb.mapper;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import org.bson.types.Decimal128;

import java.io.IOException;

/**
 * <p>
 *
 * @author Kun Yang
 * @date 2019-11-01 11:56
 */
public class MapToListSerializer extends JsonSerializer<Decimal128> {

    @Override
    public void serialize(Decimal128 value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        ObjectCodec codec = gen.getCodec();
        gen.setCodec(null);
        gen.writeObject(value);
        gen.setCodec(codec);
    }

}