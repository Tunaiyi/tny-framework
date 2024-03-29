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

package com.tny.game.codec.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.*;
import com.google.common.collect.ImmutableList;
import com.tny.game.codec.*;
import com.tny.game.codec.jackson.mapper.*;
import org.springframework.util.MimeType;

import java.lang.reflect.Type;
import java.util.Collection;

import static com.tny.game.common.collection.CollectionAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2020/8/19 6:27 下午
 */
public class JacksonObjectCodecFactory extends AbstractObjectCodecFactory {

    private final ObjectMapper mapper;

    private static final Collection<MimeType> DEFAULT_MIME_TYPES = ImmutableList
            .copyOf(MimeTypeAide.asList(JsonMimeType.JSON, JsonMimeType.JSON_WILDCARD));

    public JacksonObjectCodecFactory() {
        this(null, null);
    }

    public JacksonObjectCodecFactory(ObjectMapper mapper) {
        this(null, mapper);
    }

    public JacksonObjectCodecFactory(Collection<MimeType> supportMimeTypes) {
        this(supportMimeTypes, null);
    }

    public JacksonObjectCodecFactory(Collection<MimeType> supportMimeTypes, ObjectMapper mapper) {
        super(ifEmpty(supportMimeTypes, DEFAULT_MIME_TYPES));
        if (mapper != null) {
            this.mapper = mapper;
        } else {
            this.mapper = ObjectMapperFactory.createMapper()
                    .setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
                    .setVisibility(PropertyAccessor.SETTER, Visibility.ANY)
                    .setVisibility(PropertyAccessor.GETTER, Visibility.NONE)
                    .setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
        }
    }

    @Override
    public <T> ObjectCodec<T> createCodec(Type clazz) {
        JavaType type = this.mapper.getTypeFactory().constructType(clazz);
        return new JacksonObjectCodec<>(type, this.mapper);
    }

    @Override
    public MimeType isCanCodec(Class<?> clazz) {
        return JsonMimeType.JSON_MIME_TYPE;
    }

}
