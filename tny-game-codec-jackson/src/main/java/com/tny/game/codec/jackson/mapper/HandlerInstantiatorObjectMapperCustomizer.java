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
import com.fasterxml.jackson.databind.cfg.*;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.jsontype.*;
import com.google.common.collect.ImmutableMap;
import com.tny.game.common.utils.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/12/2 4:54 下午
 */
public class HandlerInstantiatorObjectMapperCustomizer implements ObjectMapperCustomizer {

    private final Map<Class<?>, JsonSerializer<?>> serializers;

    private final Map<Class<?>, JsonDeserializer<?>> deserializers;

    private final Map<Class<?>, KeyDeserializer> keyDeserializers;

    public HandlerInstantiatorObjectMapperCustomizer(
            List<JsonSerializer<?>> serializers,
            List<JsonDeserializer<?>> deserializers,
            List<KeyDeserializer> keyDeserializers) {
        this.serializers = ImmutableMap.copyOf(serializers.stream().collect(Collectors.toMap(
                JsonSerializer::getClass, ObjectAide::self)));
        this.deserializers = ImmutableMap.copyOf(deserializers.stream().collect(Collectors.toMap(
                JsonDeserializer::getClass, ObjectAide::self)));
        this.keyDeserializers = ImmutableMap.copyOf(keyDeserializers.stream().collect(Collectors.toMap(
                KeyDeserializer::getClass, ObjectAide::self)));
    }

    private final HandlerInstantiator instantiator = new HandlerInstantiator() {

        @Override
        public JsonDeserializer<?> deserializerInstance(DeserializationConfig config, Annotated annotated, Class<?> deserClass) {
            return deserializers.get(deserClass);
        }

        @Override
        public KeyDeserializer keyDeserializerInstance(DeserializationConfig config, Annotated annotated, Class<?> keyDeserClass) {
            return keyDeserializers.get(keyDeserClass);
        }

        @Override
        public JsonSerializer<?> serializerInstance(SerializationConfig config, Annotated annotated, Class<?> serClass) {
            return serializers.get(serClass);
        }

        @Override
        public TypeResolverBuilder<?> typeResolverBuilderInstance(MapperConfig<?> config, Annotated annotated, Class<?> builderClass) {
            return null;
        }

        @Override
        public TypeIdResolver typeIdResolverInstance(MapperConfig<?> config, Annotated annotated, Class<?> resolverClass) {
            return null;
        }
    };

    @Override
    public void customize(ObjectMapper mapper) {
        mapper.setHandlerInstantiator(instantiator);
    }

}
