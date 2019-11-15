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

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.ImmutableList;
import org.apache.commons.io.IOUtils;
import org.springframework.core.ResolvableType;
import org.springframework.http.*;
import org.springframework.http.converter.*;
import org.springframework.http.converter.json.*;
import org.springframework.util.TypeUtils;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        JavaType javaType = getJavaType(type, contextClass);
        if (!logger.isWarnEnabled()) {
            return (this.objectMapper.canDeserialize(javaType) && canRead(mediaType));
        }
        AtomicReference<Throwable> causeRef = new AtomicReference<>();
        if (this.objectMapper.canDeserialize(javaType, causeRef) && canRead(mediaType)) {
            return true;
        }
        Throwable cause = causeRef.get();
        if (cause != null) {
            String msg = "Failed to evaluate deserialization for type " + javaType;
            if (logger.isDebugEnabled()) {
                logger.warn(msg, cause);
            } else {
                logger.warn(msg + ": " + cause);
            }
        }
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        if (!logger.isWarnEnabled()) {
            return (this.objectMapper.canSerialize(clazz) && canWrite(mediaType));
        }
        AtomicReference<Throwable> causeRef = new AtomicReference<>();
        if (this.objectMapper.canSerialize(clazz, causeRef) && canWrite(mediaType)) {
            return true;
        }
        Throwable cause = causeRef.get();
        if (cause != null) {
            String msg = "Failed to evaluate serialization for type [" + clazz + "]";
            if (logger.isDebugEnabled()) {
                logger.warn(msg, cause);
            } else {
                logger.warn(msg + ": " + cause);
            }
        }
        return false;
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
            } else if (inputMessage instanceof MappingJacksonInputMessage) {
                Class<?> deserializationView = ((MappingJacksonInputMessage) inputMessage).getDeserializationView();
                if (deserializationView != null) {
                    return this.objectMapper.readerWithView(deserializationView)
                                            .withType(javaType)
                                            .readValue(inputMessage.getBody());
                }
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
            JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
            JsonGenerator generator = this.objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);
            try {
                writePrefix(generator, object);

                Class<?> serializationView = null;
                FilterProvider filters = null;
                Object value = object;
                JavaType javaType = null;
                if (object instanceof MappingJacksonValue) {
                    MappingJacksonValue container = (MappingJacksonValue) object;
                    value = container.getValue();
                    serializationView = container.getSerializationView();
                    filters = container.getFilters();
                }
                if (type != null && value != null && TypeUtils.isAssignable(type, value.getClass())) {
                    javaType = getJavaType(type, null);
                }
                ObjectWriter objectWriter;
                if (serializationView != null) {
                    objectWriter = this.objectMapper.writerWithView(serializationView);
                } else if (filters != null) {
                    objectWriter = this.objectMapper.writer(filters);
                } else {
                    objectWriter = this.objectMapper.writer();
                }
                if (javaType != null && javaType.isContainerType()) {
                    objectWriter = objectWriter.forType(javaType);
                }
                objectWriter.writeValue(generator, value);

                writeSuffix(generator, object);
                generator.flush();

            } catch (JsonProcessingException ex) {
                throw new HttpMessageNotWritableException("Could not write content: " + ex.getMessage(), ex);
            }
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


    @Override
    protected JavaType getJavaType(Type type, Class<?> contextClass) {
        TypeFactory typeFactory = this.objectMapper.getTypeFactory();
        if (type instanceof TypeVariable && contextClass != null) {
            ResolvableType resolvedType = resolveVariable(
                    (TypeVariable<?>) type, ResolvableType.forClass(contextClass));
            if (resolvedType != ResolvableType.NONE) {
                return typeFactory.constructType(resolvedType.resolve());
            }
        }
        return typeFactory.constructType(type);
    }

    private ResolvableType resolveVariable(TypeVariable<?> typeVariable, ResolvableType contextType) {
        ResolvableType resolvedType;
        if (contextType.hasGenerics()) {
            resolvedType = ResolvableType.forType(typeVariable, contextType);
            if (resolvedType.resolve() != null) {
                return resolvedType;
            }
        }
        resolvedType = resolveVariable(typeVariable, contextType.getSuperType());
        if (resolvedType.resolve() != null) {
            return resolvedType;
        }
        for (ResolvableType ifc : contextType.getInterfaces()) {
            resolvedType = resolveVariable(typeVariable, ifc);
            if (resolvedType.resolve() != null) {
                return resolvedType;
            }
        }
        return ResolvableType.NONE;
    }

}
