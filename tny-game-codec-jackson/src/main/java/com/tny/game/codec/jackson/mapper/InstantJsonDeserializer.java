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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;

import java.io.IOException;
import java.time.Instant;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/7/23 2:39 下午
 */
public class InstantJsonDeserializer extends JsonDeserializer<Instant> {

    private static final InstantJsonDeserializer INSTANT = new InstantJsonDeserializer();

    public static InstantJsonDeserializer getDefault() {
        return INSTANT;
    }

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext context) throws IOException {
        long milli = p.getValueAsLong();
        return Instant.ofEpochMilli(milli);
    }

}