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
package com.tny.game.basics.item.mapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.tny.game.basics.item.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.tny.game.common.utils.ObjectAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/19 1:20 下午
 */

public class ItemJsonSerializer extends JsonSerializer<Object> {

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeObject(null);
        } else if (value instanceof Item) {
            Item<?> item = (Item<?>) value;
            AnyId id = item.getAnyId();
            gen.writeObject(id);
        } else if (value instanceof Set) {
            Collection<Item<?>> items = as(value);
            gen.writeObject(items.stream().map(Item::getAnyId).collect(Collectors.toSet()));
        } else if (value instanceof Collection) {
            Collection<Item<?>> items = as(value);
            gen.writeObject(items.stream().map(Item::getAnyId).collect(Collectors.toList()));
        }
    }

}
