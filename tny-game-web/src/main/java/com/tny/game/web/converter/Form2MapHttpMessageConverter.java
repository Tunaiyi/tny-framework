package com.tny.game.web.converter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (!Map.class.isAssignableFrom(clazz))
            return false;
        return converter.canRead(MultiValueMap.class, mediaType);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        if (!Map.class.isAssignableFrom(clazz))
            return false;
        return converter.canWrite(MultiValueMap.class, mediaType);
    }

    @Override
    public Map<String, ?> read(Class<? extends Map<String, ?>> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        MultiValueMap<String, String> multiValueMap = converter.read(null, inputMessage);
        if (MultiValueMap.class.isAssignableFrom(clazz))
            return multiValueMap;
        Map<String, Object> map = new HashMap<>();
        multiValueMap.forEach((k, vs) -> vs.forEach(v -> map.put(k, v)));
        return map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void write(Map<String, ?> map, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (map instanceof MultiValueMap) {
            converter.write((MultiValueMap<String, ?>) map, contentType, outputMessage);
        } else {
            MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
            map.forEach(multiValueMap::add);
            converter.write(multiValueMap, contentType, outputMessage);
        }
    }
}
