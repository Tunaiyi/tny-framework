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

package com.tny.game.codec.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.tny.game.codec.*;

import java.io.*;
import java.lang.reflect.Type;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/10/9 5:46 下午
 */
public class JacksonObjectCodec<T> implements ObjectCodec<T> {

    private final ObjectMapper objectMapper;

    private final JavaType javaType;

    public JacksonObjectCodec(Type clazz, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.javaType = this.objectMapper.getTypeFactory().constructType(clazz);
    }

    @Override
    public boolean isPlaintext() {
        return true;
    }

    @Override
    public byte[] encode(T value) throws IOException {
        try {
            return this.objectMapper.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void encode(T value, OutputStream output) throws IOException {
        try {
            this.objectMapper.writeValue(output, value);
        } catch (JsonProcessingException e) {
            throw new IOException(e);
        }
    }

    @Override
    public T decode(byte[] bytes) throws IOException {
        return this.objectMapper.readValue(bytes, this.javaType);
    }

    @Override
    public T decode(InputStream input) throws IOException {
        return this.objectMapper.readValue(input, this.javaType);
    }

}
