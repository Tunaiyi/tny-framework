/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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