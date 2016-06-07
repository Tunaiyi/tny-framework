/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tny.game.web.converter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link org.springframework.http.converter.HttpMessageConverter HttpMessageConverter} that can read and write JSON using <a href="http://jackson.codehaus.org/">Jackson 2's</a> {@link ObjectMapper}.
 * <p>
 * <p>
 * This converter can be used to bind to typed beans, or untyped {@link java.util.HashMap HashMap} instances.
 * <p>
 * <p>
 * By default, this converter supports {@code application/json}. This can be overridden by setting the {@link #setSupportedMediaTypes(List) supportedMediaTypes} property.
 *
 * @author Arjen Poutsma
 * @author Keith Donald
 * @author Rossen Stoyanchev
 * @see org.springframework.web.servlet.view.json.MappingJackson2JsonView
 * @since 3.1.2
 */
public class JsonHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    public JsonHttpMessageConverter() {
        this(null, ImmutableList.of());
    }

    public JsonHttpMessageConverter(ObjectMapper objectMapper) {
        this(objectMapper, ImmutableList.of());
    }

    public JsonHttpMessageConverter(ObjectMapper objectMapper, List<MediaType> types) {
        super();
        List<MediaType> typesList = new ArrayList<>();
        typesList.add(new MediaType("application", "json", DEFAULT_CHARSET));
        typesList.add(new MediaType("application", "*+json", DEFAULT_CHARSET));
        typesList.addAll(types);
        this.setSupportedMediaTypes(typesList);
        if (objectMapper != null)
            this.setObjectMapper(objectMapper);
    }


    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        JavaType javaType = this.getJavaType(type, contextClass);
        return this.readJavaType(javaType, inputMessage);
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {
        try {
            if (javaType.getRawClass() == String.class) {
                List<String> lines = IOUtils.readLines(inputMessage.getBody());
                if (lines.size() == 1)
                    return lines.get(0);
                StringBuilder builder = new StringBuilder();
                for (String line : lines)
                    builder.append(line);
                return builder.toString();
            }
            return this.getObjectMapper().readValue(inputMessage.getBody(), javaType);
        } catch (IOException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (object instanceof String) {
            IOUtils.write(object.toString(), outputMessage.getBody());
        } else {
            super.writeInternal(object, type, outputMessage);
        }
        //		else if (object instanceof Message) {
        //			IOUtils.write(JsonFormat.printToString((Message) object), outputMessage.getBody());
        //		} else if (object instanceof Collection) {
        //			Collection<?> collection = (Collection<?>) object;
        //			boolean isMessage = false;
        //			for (Object o : collection) {
        //				isMessage = (o instanceof Message);
        //				break;
        //			}
        //			if (isMessage) {
        //				IOUtils.write(JsonFormat.printToString((Collection<Message>) collection), outputMessage.getBody());
        //			} else {
        //				super.writeInternal(object, outputMessage);
        //			}
        //		} else {
        //			super.writeInternal(object, outputMessage);
        //		}
    }
}
