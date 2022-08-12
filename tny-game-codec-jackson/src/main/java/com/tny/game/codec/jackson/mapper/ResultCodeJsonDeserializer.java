/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
