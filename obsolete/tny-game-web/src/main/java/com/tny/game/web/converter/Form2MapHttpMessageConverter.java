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

package com.tny.game.web.converter;

import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.util.*;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by Kun Yang on 16/8/23.
 */
@SuppressWarnings("rawtypes")
public class Form2MapHttpMessageConverter implements HttpMessageConverter<Map<String, ?>> {

    private FormHttpMessageConverter converter = new FormHttpMessageConverter();

    public void setCharset(Charset charset) {
        converter.setCharset(charset);
    }

    public void setMultipartCharset(Charset multipartCharset) {
        converter.setMultipartCharset(multipartCharset);
    }

    public void setSupportedMediaTypes(List<MediaType> supportedMediaTypes) {
        converter.setSupportedMediaTypes(supportedMediaTypes);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return converter.getSupportedMediaTypes();
    }

    public void setPartConverters(List<HttpMessageConverter<?>> partConverters) {
        converter.setPartConverters(partConverters);
    }

    public void addPartConverter(HttpMessageConverter<?> partConverter) {
        converter.addPartConverter(partConverter);
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        if (!Map.class.isAssignableFrom(clazz)) {
            return false;
        }
        return converter.canRead(MultiValueMap.class, mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        if (!Map.class.isAssignableFrom(clazz)) {
            return false;
        }
        return converter.canWrite(MultiValueMap.class, mediaType);
    }

    @Override
    public Map<String, ?> read(Class<? extends Map<String, ?>> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        MultiValueMap<String, String> multiValueMap = converter.read(null, inputMessage);
        if (MultiValueMap.class.isAssignableFrom(clazz)) {
            return multiValueMap;
        }
        Map<String, Object> map = new HashMap<>();
        multiValueMap.forEach((k, vs) -> vs.forEach(v -> map.put(k, v)));
        return map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(Map<String, ?> map, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        if (map instanceof MultiValueMap) {
            converter.write((MultiValueMap<String, ?>) map, contentType, outputMessage);
        } else {
            MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
            map.forEach(multiValueMap::add);
            converter.write(multiValueMap, contentType, outputMessage);
        }
    }

}
