/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
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
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Implementation of {@link org.springframework.http.converter.HttpMessageConverter HttpMessageConverter} that can read and write JSON using
 * <a href="http://jackson.codehaus.org/">Jackson 2's</a> {@link ObjectMapper}.
 * <p>
 * <p>
 * This converter can be used to bind to typed beans, or untyped {@link java.util.HashMap HashMap} instances.
 * <p>
 * <p>
 * By default, this converter supports {@code application/json}. This can be overridden by setting the {@link #setSupportedMediaTypes(List)
 * supportedMediaTypes} property.
 *
 * @author Arjen Poutsma
 * @author Keith Donald
 * @author Rossen Stoyanchev
 * @see org.springframework.web.servlet.view.json.MappingJackson2JsonView
 * @since 3.1.2
 */
public class JsonHttpMessageConverter extends MappingJackson2HttpMessageConverter {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public JsonHttpMessageConverter() {
        this(null, ImmutableList.of());
    }

    public JsonHttpMessageConverter(ObjectMapper objectMapper) {
        this(objectMapper, ImmutableList.of());
    }

    public JsonHttpMessageConverter(ObjectMapper objectMapper, List<MediaType> types) {
        super(objectMapper);
        List<MediaType> typesList = new ArrayList<>();
        typesList.add(new MediaType("application", "json", DEFAULT_CHARSET));
        typesList.add(new MediaType("application", "*+json", DEFAULT_CHARSET));
        typesList.addAll(types);
        this.setSupportedMediaTypes(typesList);
    }

    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        JavaType javaType = getJavaType(type, contextClass);
        if (!this.logger.isWarnEnabled()) {
            return (this.getObjectMapper().canDeserialize(javaType) && canRead(mediaType));
        }
        AtomicReference<Throwable> causeRef = new AtomicReference<>();
        if (this.getObjectMapper().canDeserialize(javaType, causeRef) && canRead(mediaType)) {
            return true;
        }
        Throwable cause = causeRef.get();
        if (cause != null) {
            String msg = "Failed to evaluate deserialization for type " + javaType;
            if (this.logger.isDebugEnabled()) {
                this.logger.warn(msg, cause);
            } else {
                this.logger.warn(msg + ": " + cause);
            }
        }
        return false;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        if (!this.logger.isWarnEnabled()) {
            return (this.getObjectMapper().canSerialize(clazz) && canWrite(mediaType));
        }
        AtomicReference<Throwable> causeRef = new AtomicReference<>();
        if (this.getObjectMapper().canSerialize(clazz, causeRef) && canWrite(mediaType)) {
            return true;
        }
        Throwable cause = causeRef.get();
        if (cause != null) {
            String msg = "Failed to evaluate serialization for type [" + clazz + "]";
            if (this.logger.isDebugEnabled()) {
                this.logger.warn(msg, cause);
            } else {
                this.logger.warn(msg + ": " + cause);
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
                List<String> lines = IOUtils.readLines(inputMessage.getBody(), DEFAULT_CHARSET);
                if (lines.size() == 1) {
                    return lines.get(0);
                }
                StringBuilder builder = new StringBuilder();
                for (String line : lines)
                    builder.append(line);
                return builder.toString();
            } else if (inputMessage instanceof MappingJacksonInputMessage) {
                Class<?> deserializationView = ((MappingJacksonInputMessage)inputMessage).getDeserializationView();
                if (deserializationView != null) {
                    return this.getObjectMapper().readerWithView(deserializationView)
                            .forType(javaType)
                            .readValue(inputMessage.getBody());
                }
            }
            return this.getObjectMapper().readValue(inputMessage.getBody(), javaType);
        } catch (IOException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex, inputMessage);
        }
    }

    @Override
    protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (object instanceof String) {
            IOUtils.write(object.toString(), outputMessage.getBody(), DEFAULT_CHARSET);
        } else {
            JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
            JsonGenerator generator = this.getObjectMapper().getFactory().createGenerator(outputMessage.getBody(), encoding);
            try {
                writePrefix(generator, object);

                Class<?> serializationView = null;
                FilterProvider filters = null;
                Object value = object;
                JavaType javaType = null;
                if (object instanceof MappingJacksonValue) {
                    MappingJacksonValue container = (MappingJacksonValue)object;
                    value = container.getValue();
                    serializationView = container.getSerializationView();
                    filters = container.getFilters();
                }
                if (type != null && value != null && TypeUtils.isAssignable(type, value.getClass())) {
                    javaType = getJavaType(type, null);
                }
                ObjectWriter objectWriter;
                if (serializationView != null) {
                    objectWriter = this.getObjectMapper().writerWithView(serializationView);
                } else if (filters != null) {
                    objectWriter = this.getObjectMapper().writer(filters);
                } else {
                    objectWriter = this.getObjectMapper().writer();
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
        TypeFactory typeFactory = this.getObjectMapper().getTypeFactory();
        if (type instanceof TypeVariable && contextClass != null) {
            ResolvableType resolvedType = resolveVariable(
                    (TypeVariable<?>)type, ResolvableType.forClass(contextClass));
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
