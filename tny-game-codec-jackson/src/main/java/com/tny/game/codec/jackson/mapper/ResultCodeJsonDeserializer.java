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

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.tny.game.common.result.*;

import java.io.IOException;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class ResultCodeJsonDeserializer extends JsonDeserializer<ResultCode> {

    private static final ResultCodeJsonDeserializer INSTANT = new ResultCodeJsonDeserializer();

    public static ResultCodeJsonDeserializer getDefault() {
        return INSTANT;
    }

    @Override
    public ResultCode deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return ResultCodes.of(parser.getIntValue());
    }

}
