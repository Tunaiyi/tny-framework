package com.tny.game.web.converter;

import com.google.common.collect.ImmutableList;
import org.springframework.http.*;
import org.springframework.http.converter.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by Kun Yang on 16/9/5.
 */
public class NoBodyHttpMessageConverter implements HttpMessageConverter<Object> {

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return true;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return ImmutableList.of();
    }

    @Override
    public Object read(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return null;
    }

    @Override
    public void write(Object o, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
    }

}
