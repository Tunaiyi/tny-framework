package com.tny.game.common.codec.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.*;
import com.google.common.collect.ImmutableList;
import com.tny.game.common.codec.*;
import com.tny.game.common.codec.jackson.mapper.*;
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
    public <T> ObjectCodec<T> createCodecor(Type clazz) {
        JavaType type = this.mapper.getTypeFactory().constructType(clazz);
        return new JacksonObjectCodec<>(type, this.mapper);
    }

}
