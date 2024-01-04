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

package com.tny.game.codec.jackson.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.tny.game.common.enums.*;

import java.io.IOException;

/**
 * Created by Kun Yang on 16/8/12.
 */
public class EnumerableJsonSerializer<E extends Enum<E> & Enumerable<ID>, ID> extends JsonSerializer<E> {

    public EnumerableJsonSerializer() {
    }

    @Override
    public void serialize(E value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeObject(null);
        } else {
            gen.writeObject(((Enumerable<?>) value).getId());
        }

    }

}
