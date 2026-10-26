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

package com.tny.game.basics.item.mapper;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.tny.game.basics.item.*;
import com.tny.game.basics.item.annotation.*;
import org.slf4j.*;

import java.io.IOException;
import java.util.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/10/19 1:20 下午
 */

public class ItemJsonDeserializer extends JsonDeserializer<Object> implements ContextualDeserializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(ItemJsonDeserializer.class);

    public static final TypeReference<List<AnyId>> LIST_ANY_ID_TYPE_REFERENCE = new TypeReference<List<AnyId>>() {

    };

    private final GameExplorer gameExplorer;

    private final JsonItemFormat format;

    private final Class<?> sourceClass;

    public ItemJsonDeserializer(GameExplorer gameExplorer) {
        this.gameExplorer = gameExplorer;
        this.format = null;
        this.sourceClass = null;
    }

    public ItemJsonDeserializer(GameExplorer gameExplorer, Class<?> sourceClass, JsonItemFormat format) {
        this.gameExplorer = gameExplorer;
        this.format = format;
        this.sourceClass = sourceClass;
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt) {
        if (format == null) {
            return null;
        }
        if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
            return null;
        }
        try {
            if (Collection.class.isAssignableFrom(sourceClass)) {
                List<AnyId> ids = p.readValueAs(LIST_ANY_ID_TYPE_REFERENCE);
                return gameExplorer.getItems(format.value(), ids);
            }
            if (Item.class.isAssignableFrom(sourceClass)) {
                AnyId id = p.readValueAs(AnyId.class);
                return gameExplorer.getItem(format.value(), id);
            }
        } catch (IOException e) {
            LOGGER.error("{} deserialize exception", this.sourceClass, e);
        }
        return null;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        if (property != null) { // 为空直接跳过
            JsonItemFormat managed = property.getAnnotation(JsonItemFormat.class);
            if (managed == null) {
                managed = property.getContextAnnotation(JsonItemFormat.class);
            }
            if (managed != null) { // 如果能得到注解，就将注解的 value 传入 ImageURLSerialize
                return new ItemJsonDeserializer(gameExplorer, property.getType().getRawClass(), managed);
            }
        }
        return null;
    }

}
