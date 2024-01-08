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
